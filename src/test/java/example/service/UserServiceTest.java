package example.service;

import example.entity.User;
import example.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.getAllUsers();

        // Then
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<User> actualUsers = userService.getAllUsers();

        // Then
        assertTrue(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user when user exists")
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(testUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    @DisplayName("Should return empty optional when user does not exist")
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should create and save new user")
    void createUser_ShouldCreateAndSaveUser() {
        // Given
        String name = "New User";
        String email = "newuser@example.com";
        User newUser = new User(name, email);
        newUser.setId(UUID.randomUUID());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.createUser(name, email);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertNotNull(result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update existing user")
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Given
        String updatedName = "Updated Name";
        String updatedEmail = "updated@example.com";
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(testUserId, updatedName, updatedEmail);

        // Then
        assertNotNull(result);
        assertEquals(updatedName, result.getName());
        assertEquals(updatedEmail, result.getEmail());
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        String name = "Updated Name";
        String email = "updated@example.com";
        
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUser(nonExistentId, name, email));
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user when user exists")
    void deleteUser_WhenUserExists_ShouldDeleteAndReturnTrue() {
        // Given
        when(userRepository.existsById(testUserId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(testUserId);

        // When
        boolean result = userService.deleteUser(testUserId);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsById(testUserId);
        verify(userRepository, times(1)).deleteById(testUserId);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void deleteUser_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(nonExistentId);

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }
}