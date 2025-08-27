package org.example.controller;

import org.example.service.UserService;
import org.example.service.RoleService;
import org.example.model.User;
import org.example.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            logger.info("Found {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error getting users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleService.findAll();
            logger.info("Found {} roles", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error getting roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("User not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            logger.info("Updating user ID: {}", id);
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAge(updatedUser.getAge());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                logger.info("Password updated for user ID: {}", id);
            }

            existingUser.setRoles(updatedUser.getRoles());
            User savedUser = userService.save(existingUser);
            logger.info("User successfully updated: {}", savedUser.getEmail());

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("User successfully deleted with ID: {}", id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            logger.info("Creating user: {}", user.getEmail());
            User existingUser = userService.findByEmail(user.getEmail());
            if (existingUser != null) {
                logger.warn("User with email {} already exists", user.getEmail());
                return ResponseEntity.badRequest()
                        .body("User with email " + user.getEmail() + " already exists");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userService.save(user);
            logger.info("User successfully created: {}", savedUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }
}