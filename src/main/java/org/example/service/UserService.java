package org.example.service;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void createAdminUser() {
        if (userRepository.findByEmail("admin@example.com") == null) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setAge(33);
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("12345"));

            Role adminRole = getOrCreateRole("ROLE_ADMIN");
            Role userRole = getOrCreateRole("ROLE_USER");

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }

    private Role getOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        // Автоматически шифруем пароль при сохранении
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Новые методы для REST API
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
}