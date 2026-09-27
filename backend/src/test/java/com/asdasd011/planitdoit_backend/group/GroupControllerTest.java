package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
class GroupControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @Test
    void shouldReturnGroupsForUser() throws Exception{
        when(groupService.getGroupsForUser(1L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(new GroupResponse(10L,"Test0",1L,10L,6L,new BigDecimal("60.00"))));

        mockMvc.perform(get("/api/groups")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(10)).andExpect(jsonPath("$[0].title").value("Test0")).andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void shouldReturnOneGroup() throws Exception{
        when(groupService.getGroupForUser(1L,10L)).thenReturn(new GroupResponse(10L,"Test0",1L,10L,6L,new BigDecimal("60.00")));

        mockMvc.perform(get("/api/groups/10")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test0"))
            .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldCreateGroup() throws Exception {
        when(groupService.createGroup(1L,new CreateGroupRequest("Test0"))).thenReturn(new GroupResponse(10L, "Test0", 1L,0L,0L,null));

        mockMvc.perform(post("/api/groups").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "Test0"
            }
            """)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test0")).andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldUpdateGroup() throws Exception {
        when(groupService.updateGroup(1L,10L, new UpdateGroupRequest("Test1"))).thenReturn(new GroupResponse(10L,"Test1",1L,10L,6L,new BigDecimal("60.00")));

        mockMvc.perform(put("/api/groups/10").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "Test1"
            }
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test1")).andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldDeleteGroup() throws Exception {
        doNothing().when(groupService).deleteGroup(1L, 10L);

        mockMvc.perform(delete("/api/groups/10")).andExpect(status().isNoContent());

        verify(groupService).deleteGroup(1L, 10L);
    }

    @Test
    void shouldSortGroupsByIncompleteTasksDescending()throws Exception{
        when(groupService.getGroupsForUser(1L,GroupSort.INCOMPLETE_TASKS,SortDirection.DESC)).thenReturn(List.of(
            new GroupResponse(20L,"Physics", 1L,4L,1L,new BigDecimal("25.00")),
            new GroupResponse(10L,"Mathematics", 1L,10L,6L,new BigDecimal("60.00"))));

        mockMvc.perform(get("/api/groups").param("sort","INCOMPLETE_TASKS").param("direction","DESC")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Physics"))
            .andExpect(jsonPath("$[1].title").value("Mathematics"));
    }

    @Test
    void shouldReturnGroupsWithCompletionProgress()throws Exception{
        when(groupService.getGroupsForUser(1L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(
            new GroupResponse(10L,"Mathematics",1L,10L,6L,new BigDecimal("60.00")),
            new GroupResponse(20L,"Physics",1L,4L,1L,new BigDecimal("25.00"))));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Mathematics"))
                .andExpect(jsonPath("$[0].totalTasks").value(10))
                .andExpect(jsonPath("$[0].completedTasks").value(6))
                .andExpect(jsonPath("$[0].completionPercentage").value(60.00))
                .andExpect(jsonPath("$[1].title").value("Physics"))
                .andExpect(jsonPath("$[1].completionPercentage").value(25.00));
    }

    @Test
    void shouldReturnNullProgressForEmptyGroup()throws Exception{
        when(groupService.getGroupsForUser(1L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(new GroupResponse(10L,"Mathematics",1L,0L,0L,null)));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalTasks").value(0))
                .andExpect(jsonPath("$[0].completedTasks").value(0))
                .andExpect(jsonPath("$[0].completionPercentage").doesNotExist());
    }
}