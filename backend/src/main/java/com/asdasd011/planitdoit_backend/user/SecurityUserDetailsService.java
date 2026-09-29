package com.asdasd011.planitdoit_backend.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository){this.userRepository=userRepository;}

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user not found"));
        return new AuthenticatedUser(user.getId(),user.getName(),user.getEmail(),user.getPasswordHash(),List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}