package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        // Given
        UserDto user1 = new UserDto(1L, "John Doe", "john@example.com",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        UserDto user2 = new UserDto(2L, "Jane Smith", "jane@example.com",
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));
        List<UserDto> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // When
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() throws Exception {
        // Given
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "John Doe", "john@example.com",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(Optional.of(userDto));

        // When
        ResultActions result = mockMvc.perform(get("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        Long userId = 999L;

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // When
        ResultActions result = mockMvc.perform(get("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void createUser_shouldCreateNewUser() throws Exception {
        // Given
        UserDto inputUserDto = new UserDto(null, "New User", "new@example.com", null, null);
        UserDto createdUserDto = new UserDto(1L, "New User", "new@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.createUser(any(UserDto.class))).thenReturn(createdUserDto);

        String userJson = """
            {
                "name": "New User",
                "email": "new@example.com"
            }
            """;

        // When
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new@example.com")));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(userId, "Updated Name", "updated@example.com",
                LocalDateTime.now().minusDays(2), LocalDateTime.now());

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(Optional.of(updatedUserDto));

        String updateJson = """
            {
                "name": "Updated Name",
                "email": "updated@example.com"
            }
            """;

        // When
        ResultActions result = mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void updateUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        Long userId = 999L;

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(Optional.empty());

        String updateJson = """
            {
                "name": "Non-existent User",
                "email": "nonexistent@example.com"
            }
            """;

        // When
        ResultActions result = mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson));

        // Then
        result.andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void deleteUser_shouldDeleteUserAndReturnNoContent() throws Exception {
        // Given
        Long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(true);

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", userId));

        // Then
        result.andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        Long userId = 999L;

        when(userService.deleteUser(userId)).thenReturn(false);

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", userId));

        // Then
        result.andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(userId);
    }
}