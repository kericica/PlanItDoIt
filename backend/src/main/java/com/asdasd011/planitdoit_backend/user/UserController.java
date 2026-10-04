package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import com.asdasd011.planitdoit_backend.user.dto.UpdateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController{
    private final UserService userService;

    public UserController(UserService userService){this.userService=userService;}

    @GetMapping("/me")
    public UserResponse getProfil(@AuthenticationPrincipal AuthenticatedUser user){
        return userService.getUser(user.getId());
    }

    @PutMapping("/me")
    public UserResponse updateProfile(@AuthenticationPrincipal AuthenticatedUser user,@Valid @RequestBody UpdateUserRequest request){
        return userService.updateUser(user.getId(),request);
    }
}