package example.controller;

import example.entity.User;
import example.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = userRepository.save(new User("John Doe", "john@example.com"));
    }

    @Test
    @DisplayName("Should fetch all users")
    void users_ShouldFetchAllUsers() {
        // Given
        userRepository.save(new User("Jane Smith", "jane@example.com"));

        // When & Then
        graphQlTester.document("{ users { id name email } }")
                .execute()
                .path("users")
                .entityList(User.class)
                .satisfies(users -> {
                    assertEquals(2, users.size());
                    assertTrue(users.stream().anyMatch(u -> "John Doe".equals(u.getName())));
                    assertTrue(users.stream().anyMatch(u -> "Jane Smith".equals(u.getName())));
                    // Verify all users have valid UUIDs
                    users.forEach(user -> assertNotNull(user.getId()));
                });
    }

    @Test
    @DisplayName("Should fetch user by ID")
    void user_WhenUserExists_ShouldFetchUser() {
        // When & Then
        graphQlTester.document("query($id: ID!) { user(id: $id) { id name email } }")
                .variable("id", testUser.getId().toString())
                .execute()
                .path("user")
                .entity(User.class)
                .satisfies(user -> {
                    assertEquals(testUser.getId(), user.getId());
                    assertEquals("John Doe", user.getName());
                    assertEquals("john@example.com", user.getEmail());
                });
    }

    @Test
    @DisplayName("Should return null for non-existent user")
    void user_WhenUserDoesNotExist_ShouldReturnNull() {
        // When & Then
        String nonExistentId = UUID.randomUUID().toString();
        graphQlTester.document("query($id: ID!) { user(id: $id) { id name email } }")
                .variable("id", nonExistentId)
                .execute()
                .path("user")
                .valueIsNull();
    }

    @Test
    @DisplayName("Should return null for invalid UUID format")
    void user_WhenInvalidUUID_ShouldReturnNull() {
        // When & Then
        graphQlTester.document("query($id: ID!) { user(id: $id) { id name email } }")
                .variable("id", "invalid-uuid")
                .execute()
                .path("user")
                .valueIsNull();
    }

    @Test
    @DisplayName("Should create new user")
    void createUser_ShouldCreateNewUser() {
        // When & Then
        graphQlTester.document("""
                mutation($name: String!, $email: String!) {
                    createUser(name: $name, email: $email) {
                        id
                        name
                        email
                    }
                }
                """)
                .variable("name", "New User")
                .variable("email", "newuser@example.com")
                .execute()
                .path("createUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertNotNull(user.getId());
                    assertEquals("New User", user.getName());
                    assertEquals("newuser@example.com", user.getEmail());
                    // Verify it's a valid UUID
                    assertDoesNotThrow(() -> UUID.fromString(user.getId().toString()));
                });

        // Verify user was persisted
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("Should update existing user")
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // When & Then
        graphQlTester.document("""
                mutation($id: ID!, $name: String!, $email: String!) {
                    updateUser(id: $id, name: $name, email: $email) {
                        id
                        name
                        email
                    }
                }
                """)
                .variable("id", testUser.getId().toString())
                .variable("name", "Updated Name")
                .variable("email", "updated@example.com")
                .execute()
                .path("updateUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertEquals(testUser.getId(), user.getId());
                    assertEquals("Updated Name", user.getName());
                    assertEquals("updated@example.com", user.getEmail());
                });

        // Verify user was updated in database
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    @DisplayName("Should handle invalid UUID in update mutation")
    void updateUser_WhenInvalidUUID_ShouldReturnError() {
        // When & Then
        graphQlTester.document("""
                mutation($id: ID!, $name: String!, $email: String!) {
                    updateUser(id: $id, name: $name, email: $email) {
                        id
                        name
                        email
                    }
                }
                """)
                .variable("id", "invalid-uuid")
                .variable("name", "Updated Name")
                .variable("email", "updated@example.com")
                .execute()
                .errors()
                .satisfy(errors -> assertFalse(errors.isEmpty()));
    }

    @Test
    @DisplayName("Should delete existing user")
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // When & Then
        graphQlTester.document("""
                mutation($id: ID!) {
                    deleteUser(id: $id)
                }
                """)
                .variable("id", testUser.getId().toString())
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);

        // Verify user was deleted from database
        assertFalse(userRepository.existsById(testUser.getId()));
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void deleteUser_WhenUserDoesNotExist_ShouldReturnFalse() {
        // When & Then
        String nonExistentId = UUID.randomUUID().toString();
        graphQlTester.document("""
                mutation($id: ID!) {
                    deleteUser(id: $id)
                }
                """)
                .variable("id", nonExistentId)
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertFalse);
    }

    @Test
    @DisplayName("Should return false for invalid UUID in delete")
    void deleteUser_WhenInvalidUUID_ShouldReturnFalse() {
        // When & Then
        graphQlTester.document("""
                mutation($id: ID!) {
                    deleteUser(id: $id)
                }
                """)
                .variable("id", "invalid-uuid")
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertFalse);
    }
}