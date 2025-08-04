package org.example.service;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
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

            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }

    private Role getOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}