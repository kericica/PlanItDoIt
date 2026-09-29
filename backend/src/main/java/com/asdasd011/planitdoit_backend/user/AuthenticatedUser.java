package com.asdasd011.planitdoit_backend.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AuthenticatedUser implements UserDetails{
    private final Long id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(Long id,String name,String email,String passwordHash,Collection<? extends GrantedAuthority> authorities){
        this.id=id;
        this.name=name;
        this.email=email;
        this.passwordHash=passwordHash;
        this.authorities=authorities;
    }

    public Long getId(){return id;}

    public String getName(){return name;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){return authorities;}

    @Override
    public String getPassword(){return passwordHash;}

    @Override
    public String getUsername(){return email;}
}