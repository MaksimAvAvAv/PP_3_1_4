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

    // Получить всех пользователей
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

    // Получить все роли
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

    // Получить текущего пользователя
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

    // Получить пользователя по id
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

    // Обновить пользователя (УПРОЩЕННАЯ ВЕРСИЯ)
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            logger.info("Updating user ID: {}", id);
            return ResponseEntity.ok("Update endpoint works");
        } catch (Exception e) {
            logger.error("Error in update endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Удалить пользователя
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted");
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Создать пользователя (УПРОЩЕННАЯ ВЕРСИЯ)
    @PostMapping("/admin/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            logger.info("Creating user: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("Create endpoint works");
        } catch (Exception e) {
            logger.error("Error in create endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}