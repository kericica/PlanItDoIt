package com.asdasd011.planitdoit_backend.group.dto;

import java.math.BigDecimal;

public record GroupResponse(
    Long id,
    String title,
    Long userId,
    long totalTasks,
    long completedTasks,
    BigDecimal completionPercentage
){}