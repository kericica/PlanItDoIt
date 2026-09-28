package com.asdasd011.planitdoit_backend.task.dto;

import java.math.BigDecimal;

public record GlobalTaskCompletionResponse(
    long totalTasks,
    long completedTasks,
    BigDecimal completionPercentage
){}