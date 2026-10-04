package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import com.asdasd011.planitdoit_backend.config.SecurityConfig;
import com.asdasd011.planitdoit_backend.user.SecurityUserDetailsService;
import com.asdasd011.planitdoit_backend.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthSessionSecurityTest{
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
    void loginCreatesAuthenticatedSession()throws Exception{
        AuthenticatedUser user=new AuthenticatedUser(
            42L,
            "Alice",
            "alice@example.com",
            "hashed-password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content("""
            {
                "email": "alice@example.com",
                "password": "correct-password"
            }
            """)).andExpect(status().isOk()).andExpect(authenticated()
        );
    }

    @Test
    void logoutInvalidatesAuthenticatedSession()throws Exception{
        MockHttpSession session=new MockHttpSession();

        mockMvc.perform(post("/api/auth/logout").session(session).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("alice@example.com"))
            .with(csrf())).andExpect(status().isNoContent()).andExpect(unauthenticated()
        );

        assert session.isInvalid();
    }

    @Test
    void sessionAuthenticationChangesSessionId(){
        ChangeSessionIdAuthenticationStrategy strategy=new ChangeSessionIdAuthenticationStrategy();

        MockHttpServletRequest request=new MockHttpServletRequest();
        MockHttpServletResponse response=new MockHttpServletResponse();

        MockHttpSession session=new MockHttpSession();
        request.setSession(session);

        String originalSessionId=session.getId();

        Authentication authentication =UsernamePasswordAuthenticationToken.authenticated("alice@example.com",null,List.of(new SimpleGrantedAuthority("ROLE_USER")));

        strategy.onAuthentication(authentication,request,response);

        assertThat(session.getId()).isNotEqualTo(originalSessionId);
    }
}