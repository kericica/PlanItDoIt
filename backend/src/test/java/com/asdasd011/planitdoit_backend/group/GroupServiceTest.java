package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest{
    @Mock
    private GroupRepository groupRepository;

    @Test
    void shouldReturnGroupsForUser(){
        User user=new User();
        user.setId(1L);

        Group group=new Group();
        group.setId(10L);
        group.setTitle("Test0");
        group.setUser(user);

        when(groupRepository.findByUserId(1L)).thenReturn(List.of(group));

        GroupService groupService=new GroupService(groupRepository);
        var result=groupService.getGroupsForUser(1L);

        assertEquals(1,result.size());
        assertEquals(10L,result.get(0).id());
        assertEquals("Test0",result.get(0).title());
        assertEquals(1L,result.get(0).userId());
    }
}