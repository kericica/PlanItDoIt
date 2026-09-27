package com.asdasd011.planitdoit_backend.task.dto;

import com.asdasd011.planitdoit_backend.task.TimeDifficulty;

import java.util.Map;

public record TaskCompletionSummaryResponse(
    CompletionProgress overall,
    Map<TimeDifficulty,CompletionProgress> byTimeDifficulty
){}