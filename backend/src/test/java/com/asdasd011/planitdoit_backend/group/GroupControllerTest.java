package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(GroupController.class)
class GroupControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    private AuthenticatedUser authenticatedUser() {
        return new AuthenticatedUser(
            42L,
            "Test User",
            "test@example.com",
            "encoded-password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldReturnGroupsForUser() throws Exception{
        when(groupService.getGroupsForUser(42L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(new GroupResponse(10L,"Test0",42L,10L,6L,new BigDecimal("60.00"))));

        mockMvc.perform(get("/api/groups").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser()))).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(10)).andExpect(jsonPath("$[0].title").value("Test0")).andExpect(jsonPath("$[0].userId").value(42));
    }

    @Test
    void shouldReturnOneGroup() throws Exception{
        when(groupService.getGroupForUser(42L,10L)).thenReturn(new GroupResponse(10L,"Test0",42L,10L,6L,new BigDecimal("60.00")));

        mockMvc.perform(get("/api/groups/10").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser()))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test0"))
            .andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void shouldCreateGroup() throws Exception {
        when(groupService.createGroup(42L,new CreateGroupRequest("Test0"))).thenReturn(new GroupResponse(10L, "Test0", 42L,0L,0L,null));

        mockMvc.perform(post("/api/groups").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "Test0"
            }
            """)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test0")).andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void shouldUpdateGroup() throws Exception {
        when(groupService.updateGroup(42L,10L, new UpdateGroupRequest("Test1"))).thenReturn(new GroupResponse(10L,"Test1",42L,10L,6L,new BigDecimal("60.00")));

        mockMvc.perform(put("/api/groups/10").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "Test1"
            }
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10)).andExpect(jsonPath("$.title").value("Test1")).andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void shouldDeleteGroup() throws Exception {
        doNothing().when(groupService).deleteGroup(42L, 10L);

        mockMvc.perform(delete("/api/groups/10").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).with(csrf())).andExpect(status().isNoContent());

        verify(groupService).deleteGroup(42L, 10L);
    }

    @Test
    void shouldSortGroupsByIncompleteTasksDescending()throws Exception{
        when(groupService.getGroupsForUser(42L,GroupSort.INCOMPLETE_TASKS,SortDirection.DESC)).thenReturn(List.of(
            new GroupResponse(20L,"Physics", 42L,4L,1L,new BigDecimal("25.00")),
            new GroupResponse(10L,"Mathematics", 42L,10L,6L,new BigDecimal("60.00"))));

        mockMvc.perform(get("/api/groups").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).param("sort","INCOMPLETE_TASKS").param("direction","DESC")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Physics"))
            .andExpect(jsonPath("$[1].title").value("Mathematics"));
    }

    @Test
    void shouldReturnGroupsWithCompletionProgress()throws Exception{
        when(groupService.getGroupsForUser(42L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(
            new GroupResponse(10L,"Mathematics",42L,10L,6L,new BigDecimal("60.00")),
            new GroupResponse(20L,"Physics",42L,4L,1L,new BigDecimal("25.00"))));

        mockMvc.perform(get("/api/groups").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())))
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
        when(groupService.getGroupsForUser(42L,GroupSort.TITLE,SortDirection.ASC)).thenReturn(List.of(new GroupResponse(10L,"Mathematics",42L,0L,0L,null)));

        mockMvc.perform(get("/api/groups").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalTasks").value(0))
                .andExpect(jsonPath("$[0].completedTasks").value(0))
                .andExpect(jsonPath("$[0].completionPercentage").doesNotExist());
    }
    @Test
    void shouldRejectUnauthenticatedUser()throws Exception{
        mockMvc.perform(get("/api/groups")).andExpect(status().isUnauthorized());
    }
}