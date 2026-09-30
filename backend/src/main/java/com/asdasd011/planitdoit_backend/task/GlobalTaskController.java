package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.task.dto.GlobalTaskCompletionResponse;
import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class GlobalTaskController{
    private final TaskService taskService;

    public GlobalTaskController(TaskService taskService){this.taskService=taskService;}

    
    @GetMapping("/summary")
    public GlobalTaskCompletionResponse getGlobalCompletionSummary(@AuthenticationPrincipal AuthenticatedUser user){return taskService.getGlobalCompletionSummary(user.getId());}
}