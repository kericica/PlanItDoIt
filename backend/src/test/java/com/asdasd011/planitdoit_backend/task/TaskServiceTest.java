package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.group.Group;
import com.asdasd011.planitdoit_backend.group.GroupRepository;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest{
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GroupRepository groupRepository;

    private User createUser(){
        User user=new User();
        user.setId(1L);
        user.setName("TestUser0");
        user.setEmail("tuser0@example.com");
        return user;
    }

    private Group createGroup(User user){
        Group group=new Group();
        group.setId(10L);
        group.setTitle("GroupTitle0");
        group.setUser(user);
        return group;
    }

    private Task createTask(Group group){
        Task task=new Task();
        task.setId(100L);
        task.setTitle("TaskTitle0");
        task.setNote("exercises 1-10");
        task.setSolutionDifficulty(SolutionDifficulty.MID);
        task.setTimeDifficulty(TimeDifficulty.MID);
        task.setHighlighted(true);
        task.setDeadline(LocalDate.of(2026,10,1));
        task.setTaskStatus(TaskStatus.TODO);
        task.setCreatedAt(Instant.parse("2026-09-24T10:00:00Z"));
        task.setCompletedAt(null);
        task.setGroup(group);
        return task;
    }

    @Test
    void shouldReturnTasksForGroup(){
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByGroupId(10L)).thenReturn(List.of(task));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTasksForGroup(1L,10L);

        assertEquals(1,result.size());
        assertEquals(100L,result.get(0).id());
        assertEquals("TaskTitle0",result.get(0).title());
        assertEquals("exercises 1-10",result.get(0).note());
        assertEquals(SolutionDifficulty.MID,result.get(0).solutionDifficulty());
        assertEquals(TimeDifficulty.MID,result.get(0).timeDifficulty());
        assertEquals(true,result.get(0).highlighted());
        assertEquals(LocalDate.of(2026,10,1),result.get(0).deadline());
        assertEquals(TaskStatus.TODO,result.get(0).taskStatus());
        assertEquals(10L,result.get(0).groupId());
    }

    @Test
    void shouldReturnOneTaskForGroup(){
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTaskForGroup(1L,10L,100L);

        assertEquals(100L,result.id());
        assertEquals("TaskTitle0",result.title());
        assertEquals("exercises 1-10",result.note());
        assertEquals(SolutionDifficulty.MID,result.solutionDifficulty());
        assertEquals(TimeDifficulty.MID,result.timeDifficulty());
        assertEquals(true,result.highlighted());
        assertEquals(TaskStatus.TODO,result.taskStatus());
        assertEquals(10L,result.groupId());
    }

    @Test
    void shouldCreateTask() {
        User user=createUser();
        Group group=createGroup(user);
        Task savedTask=createTask(group);
        var request=new CreateTaskRequest("TaskTitle0","exercises 1-10",SolutionDifficulty.MID,TimeDifficulty.MID,true,LocalDate.of(2026,10,1),TaskStatus.TODO,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.createTask(1L,10L,request);

        assertEquals(100L,result.id());
        assertEquals("TaskTitle0",result.title());
        assertEquals("exercises 1-10",result.note());
        assertEquals(SolutionDifficulty.MID,result.solutionDifficulty());
        assertEquals(TimeDifficulty.MID,result.timeDifficulty());
        assertEquals(true,result.highlighted());
        assertEquals(TaskStatus.TODO,result.taskStatus());
        assertEquals(10L,result.groupId());
    }

    @Test
    void shouldUpdateTask() {
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);
        var request=new UpdateTaskRequest("TaskTitle1","exercises 11-20",SolutionDifficulty.HARD,TimeDifficulty.LOT,false,LocalDate.of(2026,10,5),TaskStatus.IN_PROGRESS);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.updateTask(1L,10L,100L,request);

        assertEquals("TaskTitle1",task.getTitle());
        assertEquals("exercises 11-20",task.getNote());
        assertEquals(SolutionDifficulty.HARD,task.getSolutionDifficulty());
        assertEquals(TimeDifficulty.LOT,task.getTimeDifficulty());
        assertEquals(false,task.isHighlighted());
        assertEquals(LocalDate.of(2026,10,5),task.getDeadline());
        assertEquals(TaskStatus.IN_PROGRESS,task.getTaskStatus());
        assertEquals(100L,result.id());
        assertEquals("TaskTitle1",result.title());
        assertEquals(TaskStatus.IN_PROGRESS,result.taskStatus());
    }

    @Test
    void shouldDeleteTask() {
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        taskService.deleteTask(1L,10L,100L);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionWhenGroupDoesNotBelongToUser() {
        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.empty());

        TaskService taskService=new TaskService(taskRepository,groupRepository);

        assertThrows(ResourceNotFoundException.class,()->taskService.getTasksForGroup(1L,10L));
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotBelongToGroup() {
        User user=createUser();
        Group group=createGroup(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.empty());

        TaskService taskService=new TaskService(taskRepository,groupRepository);

        assertThrows(ResourceNotFoundException.class,()->taskService.getTaskForGroup(1L,10L,100L));
    }
}