package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.Group;
import com.asdasd011.planitdoit_backend.group.GroupRepository;
import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;

@Service
public class TaskService{
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    public TaskService(TaskRepository taskRepository,GroupRepository groupRepository){
        this.taskRepository=taskRepository;
        this.groupRepository=groupRepository;
    }

    public List<TaskResponse> getTasksForGroup(Long userId,Long groupId,TaskSort sort,SortDirection direction){
        Group group=getUserGroup(userId,groupId);
        return taskRepository.findByGroupId(group.getId(),buildSort(sort,direction)).stream().map(this::toResponse).toList();
    }

    public TaskResponse getTaskForGroup(Long userId,Long groupId,Long taskId){
        getUserGroup(userId,groupId);
        Task task=taskRepository.findByIdAndGroupId(taskId,groupId).orElseThrow(()->new ResourceNotFoundException("task not found"));
        return toResponse(task);
    }

    public TaskResponse createTask(Long userId,Long groupId,CreateTaskRequest request){
        Group group=getUserGroup(userId,groupId);
        Task task=new Task();
        task.setTitle(request.title());
        task.setNote(request.note());
        task.setSolutionDifficulty(request.solutionDifficulty());
        task.setTimeDifficulty(request.timeDifficulty());
        task.setHighlighted(request.highlighted());
        task.setDeadline(request.deadline());
        task.setCreatedAt(Instant.now());
        applyStatus(task,request.taskStatus());
        task.setGroup(group);

        Task savedTask=taskRepository.save(task);
        return toResponse(savedTask);
    }

    public TaskResponse updateTask(Long userId,Long groupId,Long taskId,UpdateTaskRequest request){
        getUserGroup(userId,groupId);
        Task task=taskRepository.findByIdAndGroupId(taskId,groupId).orElseThrow(()->new ResourceNotFoundException("task not found"));
        task.setTitle(request.title());
        task.setNote(request.note());
        task.setSolutionDifficulty(request.solutionDifficulty());
        task.setTimeDifficulty(request.timeDifficulty());
        task.setHighlighted(request.highlighted());
        task.setDeadline(request.deadline());
        applyStatus(task,request.taskStatus());

        Task updatedTask=taskRepository.save(task);
        return toResponse(updatedTask);
    }

    public void deleteTask(Long userId,Long groupId,Long taskId){
        getUserGroup(userId,groupId);
        Task task=taskRepository.findByIdAndGroupId(taskId,groupId).orElseThrow(()->new ResourceNotFoundException("task not found"));
        taskRepository.delete(task);
    }

    private void applyStatus(Task task,TaskStatus newStatus){
        TaskStatus effectiveStatus=newStatus!=null?newStatus:TaskStatus.TODO;
        TaskStatus previousStatus=task.getTaskStatus();
        task.setTaskStatus(effectiveStatus);
        if(effectiveStatus==TaskStatus.COMPLETED&&previousStatus!=TaskStatus.COMPLETED)task.setCompletedAt(Instant.now());
        else if(effectiveStatus!=TaskStatus.COMPLETED)task.setCompletedAt(null);
    }

    private Sort buildSort(TaskSort sort,SortDirection direction){
        String property=switch(sort){
            case TITLE->"title";
            case CREATED_AT->"createdAt";
            case DEADLINE->"deadline";
            case SOLUTION_DIFFICULTY->"solutionDifficulty";
        };
        Sort.Direction springDirection=direction==SortDirection.ASC?Sort.Direction.ASC:Sort.Direction.DESC;
        Sort.Order primaryOrder=new Sort.Order(springDirection,property);
        if(sort==TaskSort.DEADLINE)primaryOrder=primaryOrder.nullsLast();
        return Sort.by(primaryOrder,new Sort.Order(Sort.Direction.ASC,"id")); 
    }

    private Group getUserGroup(Long userId,Long groupId){return groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));}

    public TaskResponse toResponse(Task task){
        return new TaskResponse(task.getId(),task.getTitle(),task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),
            task.getDeadline(),task.getTaskStatus(),task.getCreatedAt(),task.getCompletedAt(),task.getGroup().getId());
    }
}