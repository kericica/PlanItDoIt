package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.group.Group;
import com.asdasd011.planitdoit_backend.group.GroupRepository;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.GlobalTaskCompletionResponse;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        when(taskRepository.findByGroupId(ArgumentMatchers.eq(10L),ArgumentMatchers.any(Sort.class))).thenReturn(List.of(task));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTasksForGroup(1L,10L,TaskSort.TITLE,SortDirection.ASC);

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
        var request=new UpdateTaskRequest("TaskTitle1","exercises 11-20",SolutionDifficulty.HARD,TimeDifficulty.LOT,false,LocalDate.of(2026,10,5),
            TaskStatus.IN_PROGRESS,10L);

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
    void shouldMoveTaskToAnotherGroup() {
        User user=createUser();

        Group oldGroup=createGroup(user);
        oldGroup.setId(10L);
        oldGroup.setTitle("Mathematics");

        Group newGroup=createGroup(user);
        newGroup.setId(20L);
        newGroup.setTitle("Physics");

        Task task=createTask(oldGroup);

        var request=new UpdateTaskRequest(task.getTitle(),task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),task.getDeadline(),
            task.getTaskStatus(),20L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(oldGroup));
        when(groupRepository.findByIdAndUserId(20L,1L)).thenReturn(Optional.of(newGroup));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.updateTask(1L,10L,100L,request);

        assertEquals(20L,task.getGroup().getId());
        assertEquals(20L,result.groupId());
    }

    @Test
    void shouldDefaultNewTaskToTodo(){
        User user=createUser();
        Group group=createGroup(user);

        var request= new CreateTaskRequest("StatusTest",null,SolutionDifficulty.EASY,TimeDifficulty.FLASH,false,null,null,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation->invocation.getArgument(0));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.createTask(1L,10L,request);

        assertEquals(TaskStatus.TODO,result.taskStatus());
        assertNull(result.completedAt());
    }

    @Test
    void shouldSetCompletedAtWhenCreatingCompletedTask(){
        User user=createUser();
        Group group=createGroup(user);

        var request=new CreateTaskRequest("CompletedStatusTest",null,SolutionDifficulty.EASY,TimeDifficulty.FLASH,false,null,TaskStatus.COMPLETED,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation->invocation.getArgument(0));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.createTask(1L,10L,request);

        assertEquals(TaskStatus.COMPLETED,result.taskStatus());
        assertNotNull(result.completedAt());
    }

    @Test
    void shouldSetCompletedAtWhenTaskBecomesCompleted(){
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setCompletedAt(null);

        var request=new UpdateTaskRequest(task.getTitle(),task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),task.getDeadline(),
            TaskStatus.COMPLETED,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.updateTask(1L,10L,100L,request);

        assertEquals(TaskStatus.COMPLETED,result.taskStatus());
        assertNotNull(result.completedAt());
    }

    @Test
    void shouldClearCompletedAtWhenTaskLeavesCompleted(){
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);
        Instant completedAt=Instant.parse("2026-09-24T10:00:00Z");
        task.setTaskStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(completedAt);

        var request=new UpdateTaskRequest(task.getTitle(),task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),task.getDeadline(),
            TaskStatus.IN_PROGRESS,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.updateTask(1L,10L,100L,request);

        assertEquals(TaskStatus.IN_PROGRESS,result.taskStatus());
        assertNull(result.completedAt());
    }

    @Test
    void shouldKeepCompletedAtWhenTaskRemainsCompleted(){
        User user=createUser();
        Group group=createGroup(user);
        Task task=createTask(group);
        Instant completedAt=Instant.parse("2026-09-24T10:00:00Z");
        task.setTaskStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(completedAt);

        var request=new UpdateTaskRequest("UpdatedTitleStatusTest",task.getNote(),task.getSolutionDifficulty(),task.getTimeDifficulty(),task.isHighlighted(),task.getDeadline(),
            TaskStatus.COMPLETED,10L);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByIdAndGroupId(100L,10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.updateTask(1L,10L,100L,request);

        assertEquals(TaskStatus.COMPLETED,result.taskStatus());
        assertEquals(completedAt,result.completedAt());
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

        assertThrows(ResourceNotFoundException.class,()->taskService.getTasksForGroup(1L,10L,TaskSort.TITLE,SortDirection.ASC));
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

    @Test
    void shouldSortTasksByDeadlineAscending(){
        User user=createUser();
        Group group=createGroup(user);

        Task firstTask=createTask(group);
        firstTask.setId(100L);
        firstTask.setTitle("First task");
        firstTask.setDeadline(LocalDate.of(2026,9,28));

        Task secondTask=createTask(group);
        secondTask.setId(200L);
        secondTask.setTitle("Second task");
        secondTask.setDeadline(LocalDate.of(2026,10,5));

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByGroupId(ArgumentMatchers.eq(10L),ArgumentMatchers.any(Sort.class))).thenReturn(List.of(firstTask,secondTask));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTasksForGroup(1L,10L,TaskSort.DEADLINE,SortDirection.ASC);

        assertEquals("First task",result.get(0).title());
        assertEquals("Second task",result.get(1).title());
    }

    @Test
    void shouldSortTasksBySolutionDifficultyDescending(){
        User user=createUser();
        Group group=createGroup(user);

        Task easyTask=createTask(group);
        easyTask.setId(100L);
        easyTask.setTitle("Easy task");
        easyTask.setSolutionDifficulty(SolutionDifficulty.EASY);

        Task hardTask=createTask(group);
        hardTask.setId(200L);
        hardTask.setTitle("Hard task");
        hardTask.setSolutionDifficulty(SolutionDifficulty.HARD);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByGroupId(ArgumentMatchers.eq(10L),ArgumentMatchers.any(Sort.class))).thenReturn(List.of(hardTask,easyTask));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTasksForGroup(1L,10L,TaskSort.SOLUTION_DIFFICULTY,SortDirection.DESC);

        assertEquals(SolutionDifficulty.HARD,result.get(0).solutionDifficulty());
        assertEquals(SolutionDifficulty.EASY,result.get(1).solutionDifficulty());
    }

    @Test
    void shouldCalculateCompletionSummary(){
        User user=createUser();
        Group group=createGroup(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.getCompletionCountsByTimeDifficulty(10L,TaskStatus.COMPLETED)).thenReturn(List.of(new Object[]{TimeDifficulty.FLASH,6L,3L},new Object[]{TimeDifficulty.MID,5L,4L},
            new Object[]{TimeDifficulty.LOT,4L,2L}));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getCompletionSummary(1L,10L);

        assertEquals(15,result.overall().totalTasks());
        assertEquals(9,result.overall().completedTasks());
        assertEquals(new BigDecimal("60.00"),result.overall().percentage());
        assertEquals(6,result.byTimeDifficulty().get(TimeDifficulty.FLASH).totalTasks());
        assertEquals(3,result.byTimeDifficulty().get(TimeDifficulty.FLASH).completedTasks());
        assertEquals(new BigDecimal("50.00"),result.byTimeDifficulty().get(TimeDifficulty.FLASH).percentage());
        assertEquals(new BigDecimal("80.00"),result.byTimeDifficulty().get(TimeDifficulty.MID).percentage());
        assertEquals(new BigDecimal("50.00"),result.byTimeDifficulty().get(TimeDifficulty.LOT).percentage());
    }

    @Test
    void shouldReturnNullPercentageForEmptyTimeDifficulty(){
        User user=createUser();
        Group group=createGroup(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.getCompletionCountsByTimeDifficulty(10L,TaskStatus.COMPLETED)).thenReturn(List.of(new Object[]{TimeDifficulty.FLASH,4L,2L},new Object[]{TimeDifficulty.LOT,2L,1L}));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getCompletionSummary(1L,10L);
        var midProgress=result.byTimeDifficulty().get(TimeDifficulty.MID);

        assertEquals(0,midProgress.totalTasks());
        assertEquals(0,midProgress.completedTasks());
        assertNull(midProgress.percentage());
    }

    @Test
    void shouldReturnNullPercentageForEmptyGroup(){
        User user=createUser();
        Group group=createGroup(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.getCompletionCountsByTimeDifficulty(10L,TaskStatus.COMPLETED)).thenReturn(List.of());

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getCompletionSummary(1L,10L);

        assertEquals(0,result.overall().totalTasks());
        assertEquals(0,result.overall().completedTasks());
        assertNull(result.overall().percentage());

        for (TimeDifficulty difficulty:TimeDifficulty.values()){
            var progress=result.byTimeDifficulty().get(difficulty);

            assertEquals(0,progress.totalTasks());
            assertEquals(0,progress.completedTasks());
            assertNull(progress.percentage());
        }
    }

    @Test
    void shouldCalculateGlobalCompletionSummary(){
        when(taskRepository.getGlobalCompletionCounts(1L,TaskStatus.COMPLETED)).thenReturn(new Object[]{20L,12L});

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        GlobalTaskCompletionResponse result=taskService.getGlobalCompletionSummary(1L);

        assertEquals(20L,result.totalTasks());
        assertEquals(12L,result.completedTasks());
        assertEquals(new BigDecimal("60.00"),result.completionPercentage());
    }

    @Test
    void shouldReturnNullPercentageWhenUserHasNoTasks(){
        when(taskRepository.getGlobalCompletionCounts(1L,TaskStatus.COMPLETED)).thenReturn(new Object[]{0L, null});

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        GlobalTaskCompletionResponse result=taskService.getGlobalCompletionSummary(1L);

        assertEquals(0L,result.totalTasks());
        assertEquals(0L,result.completedTasks());
        assertNull(result.completionPercentage());
    }
}