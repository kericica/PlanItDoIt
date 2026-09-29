package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import com.asdasd011.planitdoit_backend.user.UserService;
import com.asdasd011.planitdoit_backend.user.dto.CreateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController{
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(UserService userService,AuthenticationManager authenticationManager,SecurityContextRepository securityContextRepository){
        this.userService=userService;
        this.authenticationManager=authenticationManager;
        this.securityContextRepository=securityContextRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request){
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request,HttpServletRequest httpRequest,HttpServletResponse httpResponse){
        Authentication authenticationRequest=UsernamePasswordAuthenticationToken.unauthenticated(request.email(),request.password());
        Authentication authenticationResponse=authenticationManager.authenticate(authenticationRequest);
        SecurityContext context=SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationResponse);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context,httpRequest,httpResponse);
        AuthenticatedUser authenticatedUser=(AuthenticatedUser)authenticationResponse.getPrincipal();
        return new UserResponse(authenticatedUser.getId(),authenticatedUser.getName(),authenticatedUser.getUsername());
    }
}