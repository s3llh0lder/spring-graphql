package example.controller;

import example.entity.User;
import example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public User user(@Argument String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return userService.getUserById(uuid).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @MutationMapping
    public User createUser(@Argument String name, @Argument String email) {
        return userService.createUser(name, email);
    }

    @MutationMapping
    public User updateUser(@Argument String id, @Argument String name, @Argument String email) {
        try {
            UUID uuid = UUID.fromString(id);
            return userService.updateUser(uuid, name, email);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format");
        }
    }

    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return userService.deleteUser(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}