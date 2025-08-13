package example.controller;

import example.entity.User;
import example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private User testUser2;
    private UUID testUserId;
    private UUID testUser2Id;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser2Id = UUID.randomUUID();
        
        testUser = new User("John Doe", "john@example.com");
        testUser.setId(testUserId);
        
        testUser2 = new User("Jane Smith", "jane@example.com");
        testUser2.setId(testUser2Id);
    }

    @Test
    @DisplayName("Should return all users")
    void users_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, testUser2);
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        // When
        List<User> result = userController.users();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should return user when exists")
    void user_WhenUserExists_ShouldReturnUser() {
        // Given
        String userIdString = testUserId.toString();
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userController.user(userIdString);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals("John Doe", result.getName());
        verify(userService, times(1)).getUserById(testUserId);
    }

    @Test
    @DisplayName("Should return null when user does not exist")
    void user_WhenUserDoesNotExist_ShouldReturnNull() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        String userIdString = nonExistentId.toString();
        when(userService.getUserById(nonExistentId)).thenReturn(Optional.empty());

        // When
        User result = userController.user(userIdString);

        // Then
        assertNull(result);
        verify(userService, times(1)).getUserById(nonExistentId);
    }

    @Test
    @DisplayName("Should return null for invalid UUID format")
    void user_WhenInvalidUUID_ShouldReturnNull() {
        // Given
        String invalidUUID = "invalid-uuid";

        // When
        User result = userController.user(invalidUUID);

        // Then
        assertNull(result);
        verify(userService, never()).getUserById(any(UUID.class));
    }

    @Test
    @DisplayName("Should create new user")
    void createUser_ShouldCreateAndReturnUser() {
        // Given
        String name = "New User";
        String email = "newuser@example.com";
        User newUser = new User(name, email);
        newUser.setId(UUID.randomUUID());
        when(userService.createUser(name, email)).thenReturn(newUser);

        // When
        User result = userController.createUser(name, email);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertNotNull(result.getId());
        verify(userService, times(1)).createUser(name, email);
    }

    @Test
    @DisplayName("Should update existing user")
    void updateUser_ShouldUpdateAndReturnUser() {
        // Given
        String userIdString = testUserId.toString();
        String updatedName = "Updated Name";
        String updatedEmail = "updated@example.com";
        User updatedUser = new User(updatedName, updatedEmail);
        updatedUser.setId(testUserId);
        
        when(userService.updateUser(testUserId, updatedName, updatedEmail)).thenReturn(updatedUser);

        // When
        User result = userController.updateUser(userIdString, updatedName, updatedEmail);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        assertEquals(updatedName, result.getName());
        assertEquals(updatedEmail, result.getEmail());
        verify(userService, times(1)).updateUser(testUserId, updatedName, updatedEmail);
    }

    @Test
    @DisplayName("Should throw exception for invalid UUID in update")
    void updateUser_WhenInvalidUUID_ShouldThrowException() {
        // Given
        String invalidUUID = "invalid-uuid";
        String name = "Updated Name";
        String email = "updated@example.com";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userController.updateUser(invalidUUID, name, email));
        
        assertEquals("Invalid UUID format", exception.getMessage());
        verify(userService, never()).updateUser(any(UUID.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_WhenUserExists_ShouldReturnTrue() {
        // Given
        String userIdString = testUserId.toString();
        when(userService.deleteUser(testUserId)).thenReturn(true);

        // When
        Boolean result = userController.deleteUser(userIdString);

        // Then
        assertTrue(result);
        verify(userService, times(1)).deleteUser(testUserId);
    }

    @Test
    @DisplayName("Should return false when user does not exist for deletion")
    void deleteUser_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        String userIdString = nonExistentId.toString();
        when(userService.deleteUser(nonExistentId)).thenReturn(false);

        // When
        Boolean result = userController.deleteUser(userIdString);

        // Then
        assertFalse(result);
        verify(userService, times(1)).deleteUser(nonExistentId);
    }

    @Test
    @DisplayName("Should return false for invalid UUID in delete")
    void deleteUser_WhenInvalidUUID_ShouldReturnFalse() {
        // Given
        String invalidUUID = "invalid-uuid";

        // When
        Boolean result = userController.deleteUser(invalidUUID);

        // Then
        assertFalse(result);
        verify(userService, never()).deleteUser(any(UUID.class));
    }
}