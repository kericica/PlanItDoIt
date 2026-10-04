package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.exception.InvalidCurrentPasswordException;
import com.asdasd011.planitdoit_backend.exception.ResourceAlreadyExistsException;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.user.dto.ChangePasswordRequest;
import com.asdasd011.planitdoit_backend.user.dto.CreateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UpdateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public UserResponse register(CreateUserRequest request){
        if(userRepository.findByEmail(request.email()).isPresent())throw new ResourceAlreadyExistsException("email is already registered");
        User user=new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        User savedUser=userRepository.save(user);
        return toResponse(savedUser);
    }

    public UserResponse getUser(Long userId){
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
        return toResponse(user);
    }

    public UserResponse updateUser(Long userId,UpdateUserRequest request){
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
        Optional<User> userWithEmail=userRepository.findByEmail(request.email());
        if(userWithEmail.isPresent()&& !userWithEmail.get().getId().equals(userId))throw new ResourceAlreadyExistsException("email is already registered");
        user.setName(request.name());
        user.setEmail(request.email());
        User savedUser=userRepository.save(user);
        return toResponse(savedUser);
    }

    public void changePassword(Long userId,ChangePasswordRequest request){
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
        if(!passwordEncoder.matches(request.currentPassword(),user.getPasswordHash()))throw new InvalidCurrentPasswordException("incorrect password");
        String newPasswordHash=passwordEncoder.encode(request.newPassword());
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user){
        return new UserResponse(user.getId(),user.getName(),user.getEmail());
    }
}