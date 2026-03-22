package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setCreatedAt(LocalDateTime.now().minusDays(2));
        user1.setUpdatedAt(LocalDateTime.now().minusDays(1));

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setCreatedAt(LocalDateTime.now().minusDays(3));
        user2.setUpdatedAt(LocalDateTime.now().minusDays(2));

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("John Doe", dto1.getName());
        assertEquals("john@example.com", dto1.getEmail());

        UserDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Jane Smith", dto2.getName());
        assertEquals("jane@example.com", dto2.getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setCreatedAt(LocalDateTime.now().minusDays(2));
        user.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<UserDto> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        UserDto dto = result.get();
        assertEquals(userId, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_shouldReturnEmptyWhenUserDoesNotExist() {
        // Given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<UserDto> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser_shouldCreateNewUser() {
        // Given
        UserDto inputDto = new UserDto();
        inputDto.setName("New User");
        inputDto.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("New User");
        savedUser.setEmail("new@example.com");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDto result = userService.createUser(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New User", result.getName());
        assertEquals("new@example.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        // Given
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setCreatedAt(LocalDateTime.now().minusDays(2));
        existingUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setCreatedAt(LocalDateTime.now().minusDays(2));
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        Optional<UserDto> result = userService.updateUser(userId, updateDto);

        // Then
        assertTrue(result.isPresent());
        UserDto dto = result.get();
        assertEquals(userId, dto.getId());
        assertEquals("Updated Name", dto.getName());
        assertEquals("updated@example.com", dto.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldReturnEmptyWhenUserDoesNotExist() {
        // Given
        Long userId = 999L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Non-existent User");
        updateDto.setEmail("nonexistent@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<UserDto> result = userService.updateUser(userId, updateDto);

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUserAndReturnTrue() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertTrue(result);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_shouldReturnFalseWhenUserDoesNotExist() {
        // Given
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertFalse(result);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}