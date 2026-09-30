package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.sort.SortDirection;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.TaskCompletionSummaryResponse;
import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/tasks")
public class TaskController{
    private final TaskService taskService;

    public TaskController(TaskService taskService){this.taskService=taskService;}

    @GetMapping
    public List<TaskResponse> getTasks(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@RequestParam(defaultValue="TITLE") TaskSort sort,@RequestParam(defaultValue="ASC") SortDirection direction) {
        return taskService.getTasksForGroup(user.getId(),groupId,sort,direction);
    }

    @GetMapping("/summary")
    public TaskCompletionSummaryResponse getCompletionSummary(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId){return taskService.getCompletionSummary(user.getId(),groupId);}

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@PathVariable Long taskId){return taskService.getTaskForGroup(user.getId(),groupId,taskId);}
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@RequestBody CreateTaskRequest request){return taskService.createTask(user.getId(),groupId,request);}

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@PathVariable Long taskId,@RequestBody UpdateTaskRequest request){return taskService.updateTask(user.getId(),groupId,taskId,request);}

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@PathVariable Long taskId){taskService.deleteTask(user.getId(),groupId,taskId);}
}