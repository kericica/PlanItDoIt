package com.asdasd011.planitdoit_backend.user;

import com.asdasd011.planitdoit_backend.user.dto.UpdateUserRequest;
import com.asdasd011.planitdoit_backend.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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
    void shouldReturnAuthenticatedUserProfile()throws Exception{
        when(userService.getUser(42L)).thenReturn(new UserResponse(42L,"Test User","test@example.com"));

        mockMvc.perform(get("/api/users/me").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser()))).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.passwordHash").doesNotExist()
        );

        verify(userService).getUser(42L);
    }

    @Test
    void shouldUpdateAuthenticatedUserProfile()throws Exception{
        UpdateUserRequest request=new UpdateUserRequest("Updated User","updated@example.com");

        when(userService.updateUser(42L,request)).thenReturn(new UserResponse(42L,"Updated User","updated@example.com"));

        mockMvc.perform(put("/api/users/me").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Updated User",
                "email": "updated@example.com"
            }
            """)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("Updated User"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.passwordHash").doesNotExist()
        );

        verify(userService).updateUser(42L,request);
    }

    @Test
    void shouldRejectInvalidProfileUpdate()throws Exception{
        mockMvc.perform(put("/api/users/me").with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser())).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "",
                "email": "not-an-email"
            }
            """)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value("Validation failed")
        );
    }

    @Test
    void shouldRejectUnauthenticatedProfileRequest()throws Exception{
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedProfileUpdate() throws Exception {
        mockMvc.perform(put("/api/users/me").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
            {
                "name": "Updated User",
                "email": "updated@example.com"
            }
            """)).andExpect(status().isUnauthorized()
        );
    }
}