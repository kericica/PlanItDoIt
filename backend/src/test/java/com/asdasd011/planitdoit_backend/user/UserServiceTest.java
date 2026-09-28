package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.exception.ResourceAlreadyExistsException;
import com.asdasd011.planitdoit_backend.user.dto.CreateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest{
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser(){
        CreateUserRequest request=new CreateUserRequest("Alice","alice@example.com","securepassword");

        User savedUser=new User();
        savedUser.setId(1L);
        savedUser.setName("Alice");
        savedUser.setEmail("alice@example.com");
        savedUser.setPasswordHash("encoded-password");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("securepassword")).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserService userService=new UserService(userRepository,passwordEncoder);
        UserResponse result=userService.register(request);

        assertEquals(1L,result.id());
        assertEquals("Alice",result.name());
        assertEquals("alice@example.com",result.email());

        verify(passwordEncoder).encode("securepassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail(){
        User existingUser=new User();
        existingUser.setId(1L);
        existingUser.setEmail("alice@example.com");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existingUser));

        UserService userService=new UserService(userRepository,passwordEncoder);

        assertThrows(ResourceAlreadyExistsException.class,()->userService.register(new CreateUserRequest("Alice","alice@example.com","securepassword")));

        verify(passwordEncoder,never()).encode(any());
        verify(userRepository,never()).save(any());
    }
}