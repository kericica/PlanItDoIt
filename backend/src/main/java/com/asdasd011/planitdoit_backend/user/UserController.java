package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import com.asdasd011.planitdoit_backend.user.dto.ChangePasswordRequest;
import com.asdasd011.planitdoit_backend.user.dto.UpdateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal AuthenticatedUser user,@Valid @RequestBody ChangePasswordRequest request,
        Authentication authentication,HttpServletRequest httpRequest,HttpServletResponse httpResponse){
            userService.changePassword(user.getId(),request);
            SecurityContextLogoutHandler logoutHandler=new SecurityContextLogoutHandler();
            logoutHandler.logout(httpRequest,httpResponse,authentication);
        }
}