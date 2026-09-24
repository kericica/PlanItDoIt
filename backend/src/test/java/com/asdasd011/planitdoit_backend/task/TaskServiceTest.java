package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.group.Group;
import com.asdasd011.planitdoit_backend.group.GroupRepository;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest{
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GroupRepository groupRepository;

    @Test
    void shouldReturnTasksForGroup(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("GroupTitle0");
        group.setUser(user);

        Task task=new Task();
        task.setId(100L);
        task.setTitle("TaskTitle0");
        task.setNote("exercises 1-10");
        task.setGroup(group);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(taskRepository.findByGroupId(10L)).thenReturn(List.of(task));

        TaskService taskService=new TaskService(taskRepository,groupRepository);
        var result=taskService.getTasksForGroup(1L,10L);

        assertEquals(1,result.size());
        assertEquals(100L,result.get(0).id());
        assertEquals("TaskTitle0",result.get(0).title());
        assertEquals("exercises 1-10",result.get(0).note());
        assertEquals(10L,result.get(0).groupId());
    }

    @Test
    void shouldThrowExceptionWhenGroupDoesNotBelongToUser(){
        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.empty());

        TaskService taskService=new TaskService(taskRepository,groupRepository);

        assertThrows(ResourceNotFoundException.class,()->taskService.getTasksForGroup(1L,10L));
    }
}