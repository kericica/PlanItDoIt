package com.asdasd011.planitdoit_backend.task.dto;

import com.asdasd011.planitdoit_backend.task.SolutionDifficulty;
import com.asdasd011.planitdoit_backend.task.TimeDifficulty;
import com.asdasd011.planitdoit_backend.task.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String note,
    SolutionDifficulty solutionDifficulty,
    TimeDifficulty timeDifficulty,
    Boolean highlighted,
    LocalDate deadline,
    TaskStatus taskStatus,
    Instant createdAt,
    Instant completedAt,
    Long groupId
){}