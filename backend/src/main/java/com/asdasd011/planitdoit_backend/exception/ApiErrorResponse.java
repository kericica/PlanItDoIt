package com.asdasd011.planitdoit_backend.exception;

import java.time.Instant;

public record ApiErrorResponse(
    int status,
    String message,
    Instant timestamp
){}
