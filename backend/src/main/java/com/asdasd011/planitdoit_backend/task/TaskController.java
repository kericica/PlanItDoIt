package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.TaskCompletionSummaryResponse;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/tasks")
public class TaskController{
    private final TaskService taskService;

    public TaskController(TaskService taskService){this.taskService=taskService;}

    @GetMapping
    public List<TaskResponse> getTasks(@PathVariable Long groupId,@RequestParam(defaultValue="TITLE") TaskSort sort,@RequestParam(defaultValue="ASC") SortDirection direction) {
        return taskService.getTasksForGroup(1L,groupId,sort,direction);
    }

    @GetMapping("/summary")
    public TaskCompletionSummaryResponse getCompletionSummary(@PathVariable Long groupId){return taskService.getCompletionSummary(1L,groupId);}

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable Long groupId,@PathVariable Long taskId){return taskService.getTaskForGroup(1L,groupId,taskId);}
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@PathVariable Long groupId,@RequestBody CreateTaskRequest request){return taskService.createTask(1L,groupId,request);}

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(@PathVariable Long groupId,@PathVariable Long taskId,@RequestBody UpdateTaskRequest request){return taskService.updateTask(1L,groupId,taskId,request);}

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long groupId,@PathVariable Long taskId){taskService.deleteTask(1L,groupId,taskId);}
}