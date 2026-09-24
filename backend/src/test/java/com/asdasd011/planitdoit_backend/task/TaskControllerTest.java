package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldReturnTasksForGroup() throws Exception {
        when(taskService.getTasksForGroup(1L,10L)).thenReturn(List.of(new TaskResponse(100L,"TaskTitle0","exercises 1-10",null,null,false,null,null,null,null,10L)));

        mockMvc.perform(get("/api/groups/10/tasks")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(100)).andExpect(jsonPath("$[0].title").value("TaskTitle0")).andExpect(jsonPath("$[0].note").value("exercises 1-10"))
            .andExpect(jsonPath("$[0].highlighted").value(false)).andExpect(jsonPath("$[0].groupId").value(10));
    }

    @Test
    void shouldReturnNotFoundWhenGroupDoesNotBelongToUser() throws Exception {
        when(taskService.getTasksForGroup(1L,10L)).thenThrow(new ResourceNotFoundException("group not found"));

        mockMvc.perform(get("/api/groups/10/tasks")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value("group not found"));
    }
}