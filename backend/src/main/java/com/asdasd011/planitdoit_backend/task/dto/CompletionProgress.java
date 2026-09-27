package com.asdasd011.planitdoit_backend.task.dto;

import java.math.BigDecimal;

public record CompletionProgress(
    long totalTasks,
    long completedTasks,
    BigDecimal percentage
){}