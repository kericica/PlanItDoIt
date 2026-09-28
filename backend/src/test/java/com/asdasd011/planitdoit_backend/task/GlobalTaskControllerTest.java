package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.task.dto.GlobalTaskCompletionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalTaskController.class)
class GlobalTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldReturnGlobalCompletionSummary()throws Exception{
        when(taskService.getGlobalCompletionSummary(1L)).thenReturn(new GlobalTaskCompletionResponse(20L,12L,new BigDecimal("60.00")));

        mockMvc.perform(get("/api/tasks/summary"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalTasks").value(20))
            .andExpect(jsonPath("$.completedTasks").value(12))
            .andExpect(jsonPath("$.completionPercentage").value(60.00));
    }

    @Test
    void shouldReturnNullPercentageWhenThereAreNoTasks()throws Exception{
        when(taskService.getGlobalCompletionSummary(1L)).thenReturn(new GlobalTaskCompletionResponse(0L,0L,null));

        mockMvc.perform(get("/api/tasks/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasks").value(0))
            .andExpect(jsonPath("$.completedTasks").value(0))
            .andExpect(jsonPath("$.completionPercentage").doesNotExist());
    }
}