package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.exception.ResourceAlreadyExistsException;
import com.asdasd011.planitdoit_backend.user.dto.CreateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return new UserResponse(savedUser.getId(),savedUser.getName(),savedUser.getEmail());
    }
}