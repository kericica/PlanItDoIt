package com.asdasd011.planitdoit_backend.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name="users")
public class User{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name="password_hash")
    private String passwordHash;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}

    public String getPassword(){return passwordHash;}
    public void setPassword(String passwordHash){this.passwordHash=passwordHash;}

}