package com.asdasd011.planitdoit_backend.config;

import com.asdasd011.planitdoit_backend.user.SecurityUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

import java.beans.BeanProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public DaoAuthenticationProvider authenticationProvider(SecurityUserDetailsService userDetailsService,PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider){
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,SecurityContextRepository securityContextRepository)throws Exception{
        http.csrf(csrf->csrf.disable()).securityContext(securityContext->securityContext.securityContextRepository(securityContextRepository))
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation(sessionFixation->
            sessionFixation.changeSessionId())).authorizeHttpRequests(authorize->authorize.requestMatchers(
                "/api/auth/register",
                "/api/auth/login",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
            ).permitAll().anyRequest().authenticated())
            .logout(logout->logout.logoutUrl("/api/auth/logout").logoutSuccessHandler((request,response,authentication)->response.setStatus(HttpServletResponse.SC_NO_CONTENT))
        );
        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository(){return new HttpSessionSecurityContextRepository();}

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(){return new ChangeSessionIdAuthenticationStrategy();}
}