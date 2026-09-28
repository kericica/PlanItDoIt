package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.user.UserService;
import com.asdasd011.planitdoit_backend.user.dto.CreateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController{
    private final UserService userService;

    public AuthController(UserService userService){this.userService=userService;}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request){
        return userService.register(request);
    }
}