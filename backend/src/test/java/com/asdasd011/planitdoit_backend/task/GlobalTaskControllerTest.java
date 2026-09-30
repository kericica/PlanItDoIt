package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.task.dto.GlobalTaskCompletionResponse;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(GlobalTaskController.class)
class GlobalTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private AuthenticatedUser authenticatedUser(){
        return new AuthenticatedUser(
            42L,
            "Test User",
            "test@example.com",
            "encoded-password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldReturnGlobalCompletionSummary()throws Exception{
        when(taskService.getGlobalCompletionSummary(42L)).thenReturn(new GlobalTaskCompletionResponse(20L,12L,new BigDecimal("60.00")));

        mockMvc.perform(get("/api/tasks/summary").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalTasks").value(20))
            .andExpect(jsonPath("$.completedTasks").value(12))
            .andExpect(jsonPath("$.completionPercentage").value(60.00));
    }

    @Test
    void shouldReturnNullPercentageWhenThereAreNoTasks()throws Exception{
        when(taskService.getGlobalCompletionSummary(42L)).thenReturn(new GlobalTaskCompletionResponse(0L,0L,null));

        mockMvc.perform(get("/api/tasks/summary").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasks").value(0))
            .andExpect(jsonPath("$.completedTasks").value(0))
            .andExpect(jsonPath("$.completionPercentage").doesNotExist());
    }

    @Test
    void shouldRejectUnauthenticatedUser()throws Exception{
        mockMvc.perform(get("/api/tasks/summary")).andExpect(status().isUnauthorized());
    }
}