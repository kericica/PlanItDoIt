package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.user.UserRepository;
import com.asdasd011.planitdoit_backend.task.TaskRepository;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest{
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

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

    @Test
    void shouldReturnGroupsForUser(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("Test0");
        group.setUser(user);

        when(groupRepository.findByUserId(1L,Sort.by(Sort.Order.asc("title"),Sort.Order.asc("id")))).thenReturn(List.of(group));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.getGroupsForUser(1L,GroupSort.TITLE,SortDirection.ASC);

        assertEquals(1,result.size());
        assertEquals(10L,result.get(0).id());
        assertEquals("Test0",result.get(0).title());
        assertEquals(1L,result.get(0).userId());
    }

    @Test
    void shouldReturnOneGroupForUser(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("Test0");
        group.setUser(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.getGroupForUser(1L,10L);

        assertEquals(10L,result.id());
        assertEquals("Test0",result.title());
        assertEquals(1L,result.userId());
    }

    @Test
    void shouldCreateGroup(){
        User user=new User();
        user.setId(1L);

        Group savedGroup=new Group();
        savedGroup.setId(10L);
        savedGroup.setTitle("Test0");
        savedGroup.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.save(org.mockito.ArgumentMatchers.any(Group.class))).thenReturn(savedGroup);

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.createGroup(1L,new CreateGroupRequest("Test0"));

        assertEquals(10L,result.id());
        assertEquals("Test0",result.title());
        assertEquals(1L,result.userId());        
    }

    @Test
    void shouldUpdateGroup(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("Test0");
        group.setUser(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));
        when(groupRepository.save(group)).thenReturn(group);

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.updateGroup(1L,10L,new UpdateGroupRequest("NewTitle"));

        assertEquals(10L,result.id());
        assertEquals("NewTitle",result.title());
        assertEquals(1L,result.userId());
    }

    @Test
    void shouldDeleteGroup(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("Test0");
        group.setUser(user);

        when(groupRepository.findByIdAndUserId(10L,1L)).thenReturn(Optional.of(group));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        groupService.deleteGroup(1L,10L);

        verify(groupRepository).delete(group);
    }

    @Test
    void shouldSortGroupsByTitleDescending(){
        User user=createUser();

        Group mathematics=createGroup(user);
        mathematics.setId(10L);
        mathematics.setTitle("Mathematics");

        Group physics=createGroup(user);
        physics.setId(20L);
        physics.setTitle("Physics");

        when(groupRepository.findByUserId(1L,Sort.by(Sort.Order.desc("title"),Sort.Order.asc("id")))).thenReturn(List.of(physics,mathematics));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.getGroupsForUser(1L,GroupSort.TITLE,SortDirection.DESC);

        assertEquals("Physics",result.get(0).title());
        assertEquals("Mathematics",result.get(1).title());
    }

    @Test
    void shouldSortGroupsByIncompleteTaskCountDescending(){
        User user=createUser();

        Group mathematics=createGroup(user);
        mathematics.setId(10L);
        mathematics.setTitle("Mathematics");

        Group physics=createGroup(user);
        physics.setId(20L);
        physics.setTitle("Physics");

        when(groupRepository.findByUserId(1L)).thenReturn(List.of(mathematics,physics));
        when(taskRepository.countIncompleteTasksByGroupForUser(1L)).thenReturn(List.of(new Object[]{10L,2L},new Object[]{20L,5L}));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.getGroupsForUser(1L,GroupSort.INCOMPLETE_TASKS,SortDirection.DESC);

        assertEquals("Physics",result.get(0).title());
        assertEquals("Mathematics",result.get(1).title());
    }

    @Test
    void shouldSortGroupsByIncompleteTaskCountAscending(){
        User user=createUser();

        Group mathematics=createGroup(user);
        mathematics.setId(10L);
        mathematics.setTitle("Mathematics");

        Group physics=createGroup(user);
        physics.setId(20L);
        physics.setTitle("Physics");

        when(groupRepository.findByUserId(1L)).thenReturn(List.of(mathematics,physics));
        when(taskRepository.countIncompleteTasksByGroupForUser(1L)).thenReturn(List.of(new Object[]{10L,2L},new Object[]{20L,5L}));

        GroupService groupService=new GroupService(groupRepository,userRepository,taskRepository);
        var result=groupService.getGroupsForUser(1L,GroupSort.INCOMPLETE_TASKS,SortDirection.ASC);

        assertEquals("Mathematics",result.get(0).title());
        assertEquals("Physics",result.get(1).title());
    }
}