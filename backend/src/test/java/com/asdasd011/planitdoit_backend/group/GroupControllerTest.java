package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        when(groupService.getGroupsForUser(1L)).thenReturn(List.of(new GroupResponse(10L,"Test0",1L)));

        mockMvc.perform(get("/api/groups")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$[0].id").value(10)).andExpect(jsonPath("$[0].title").value("Test0")).andExpect(jsonPath("$[0].userId").value(1));
    }
}