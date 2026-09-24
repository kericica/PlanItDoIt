package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/tasks")
public class TaskController{
    private final TaskService taskService;

    public TaskController(TaskService taskService){this.taskService=taskService;}

    @GetMapping
    public List<TaskResponse> getTasks(@PathVariable Long groupId){return taskService.getTasksForGroup(1L,groupId);}
}