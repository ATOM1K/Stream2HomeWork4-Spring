package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findById_shouldReturnUserWhenExists() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Сохраняем сущность через TestEntityManager
        entityManager.persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findById_shouldReturnEmptyWhenUserDoesNotExist() {
        // When
        Optional<User> result = userRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUserWhenEmailExists() {
        // Given
        User user = new User();
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");

        entityManager.persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findByEmail("jane@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Jane Smith");
        assertThat(result.get().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenEmailDoesNotExist() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistUser() {
        // Given
        User user = new User();
        user.setName("New User");
        user.setEmail("new@example.com");

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void existsById_shouldReturnTrueWhenUserExists() {
        // Given
        User user = new User();
        user.setName("Existing User");
        user.setEmail("existing@example.com");

        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsById(user.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseWhenUserDoesNotExist() {
        // When
        boolean exists = userRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_shouldDeleteUser() {
        // Given
        User user = new User();
        user.setName("To Be Deleted");
        user.setEmail("delete@example.com");

        entityManager.persistAndFlush(user);
        Long userId = user.getId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Given
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // When
        var users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("User One", "User Two");
        assertThat(users)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    void updateUser_shouldUpdateUserFields() {
        // Given
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        entityManager.persistAndFlush(user);

        // When
        user.setName("Updated Name");
        user.setEmail("updated@example.com");
        User updatedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getUpdatedAt())
                .isAfter(updatedUser.getCreatedAt());
    }
}