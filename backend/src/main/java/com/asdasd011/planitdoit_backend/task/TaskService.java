package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.Group;
import com.asdasd011.planitdoit_backend.group.GroupRepository;
import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService{
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    public TaskService(TaskRepository taskRepository,GroupRepository groupRepository){
        this.taskRepository=taskRepository;
        this.groupRepository=groupRepository;
    }

    public List<TaskResponse> getTasksForGroup(Long userId,Long groupId){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        return taskRepository.findByGroupId(group.getId()).stream().map(this::toResponse).toList();
    }

    public TaskResponse toResponse(Task task){
        return new TaskResponse(task.getId(),task.getTitle(),task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),
            task.getDeadline(),task.getTaskStatus(),task.getCreatedAt(),task.getCompletedAt(),task.getGroup().getId());
    }
}