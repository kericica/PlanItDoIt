package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.config.SecurityConfig;
import com.asdasd011.planitdoit_backend.user.SecurityUserDetailsService;
import com.asdasd011.planitdoit_backend.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class LogoutSecurityTest{
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
    void shouldLogoutAuthenticatedUser()throws Exception {
        mockMvc.perform(post("/api/auth/logout").with(user("alice@example.com").roles("USER")).with(csrf())).andExpect(status().isNoContent()).andExpect(unauthenticated());
    }
}