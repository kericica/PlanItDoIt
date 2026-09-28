package com.asdasd011.planitdoit_backend.auth;

import com.asdasd011.planitdoit_backend.exception.ResourceAlreadyExistsException;
import com.asdasd011.planitdoit_backend.user.UserService;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRegisterUser()throws Exception{
        when(userService.register(any())).thenReturn(new UserResponse(1L,"Alice","alice@example.com"));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "securepassword"
            }
            """)).andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist())
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldRejectInvalidRegistrationData()throws Exception{
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "",
                "email": "not-an-email",
                "password": "short"
            }
            """)).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("validation failed"));
    }

    @Test
    void shouldReturnConflictForDuplicateEmail()throws Exception{
        when(userService.register(any())).thenThrow(new ResourceAlreadyExistsException("Email is already registered"));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "securepassword"
            }
        """)).andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("Email is already registered"));
    }
}