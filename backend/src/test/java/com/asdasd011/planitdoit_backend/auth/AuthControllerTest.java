package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.config.SecurityConfig;
import org.springframework.context.annotation.Import;
import com.asdasd011.planitdoit_backend.user.SecurityUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.asdasd011.planitdoit_backend.exception.ResourceAlreadyExistsException;
import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import com.asdasd011.planitdoit_backend.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser()throws Exception{
        when(userService.register(any())).thenReturn(new UserResponse(1L,"Alice","alice@example.com"));

        mockMvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "securepassword"
            }
            """)).andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist())
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldRejectInvalidRegistrationData()throws Exception{
        mockMvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "",
                "email": "not-an-email",
                "password": "short"
            }
            """)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldReturnConflictForDuplicateEmail()throws Exception{
        when(userService.register(any())).thenThrow(new ResourceAlreadyExistsException("Email is already registered"));

        mockMvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "securepassword"
            }
        """)).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    void shouldLoginUser()throws Exception{
        AuthenticatedUser authenticatedUser=new AuthenticatedUser(
                1L,
                "Alice",
                "alice@example.com",
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication authentication=UsernamePasswordAuthenticationToken.authenticated(authenticatedUser,null,authenticatedUser.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        mockMvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "email": "alice@example.com",
                "password": "securepassword"
            }
            """)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.passwordHash").doesNotExist()).andExpect(authenticated()
        );

        verify(authenticationManager).authenticate(argThat(auth->auth.getName().equals("alice@example.com")&&auth.getCredentials().equals("securepassword")));
    }

    @Test
    void shouldRejectInvalidCredentials()throws Exception{
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "email": "alice@example.com",
                "password": "wrongpassword"
            }
            """)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldRejectInvalidLoginRequest()throws Exception{
        mockMvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "email": "not-an-email",
                "password": ""
            }""")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value("Validation failed"));
    }
}