package com.asdasd011.planitdoit_backend.user.dto;

public record CreateUserRequest(
    String name,
    String email,
    String password
){}