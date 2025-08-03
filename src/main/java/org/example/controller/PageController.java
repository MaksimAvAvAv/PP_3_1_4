package org.example.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.service.UserService;
import org.example.service.RoleService;
import org.example.model.User;
import org.example.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PageController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin";
    }

    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        // Проверяем роль пользователя
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }

        model.addAttribute("users", Collections.singletonList(user));
        return "user";
    }

    @GetMapping("/admin/custom-error")
    public String customErrorPage() {
        return "error";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);

        List<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);

        List<String> userRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        model.addAttribute("userRoles", userRoles);

        return "edit-user";
    }

    @PostMapping("/admin/users/update")
    public String updateUser(@ModelAttribute("user") User updatedUser,
                             @RequestParam("roleIds") List<Long> roleIds) {
        User currentUser = userService.findById(updatedUser.getId());

        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setAge(updatedUser.getAge());

        Set<Role> roles = roleIds.stream()
                .map(roleId -> {
                    Role role = new Role();
                    role.setId(roleId);
                    return role;
                }).collect(Collectors.toSet());

        currentUser.setRoles(roles);


        currentUser.setRolesString(roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(", ")));

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            currentUser.setPassword(encodedPassword);
            logger.info("Пароль пользователя с ID {} был обновлен.", currentUser.getId());
        } else {
            logger.info("Пароль пользователя с ID {} не был изменен.", currentUser.getId());
        }

        userService.updateUser(currentUser);

        logger.info("Пользователь с ID {} обновлен с ролями: {}", currentUser.getId(), roles.stream().map(Role::getName).collect(Collectors.toList()));

        return "redirect:/admin";
    }

    @PostMapping("/admin/users/delete/{id}") // Теперь доступен по /admin/users/delete/{id}
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);

        logger.info("Пользователь с ID {} был удален.", id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/users/new")
    public String showAddUserForm(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        List<Role> roles = roleService.findAll();

        model.addAttribute("allRoles", roles);

        logger.info("Передано {} ролей в модель для добавления нового пользователя.", roles.size());

        return "add-user";
    }

    @PostMapping("/admin/users/save")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                    .map(roleId -> {
                        Role role = new Role();
                        role.setId(roleId);
                        return role;
                    }).collect(Collectors.toSet());

            user.setRoles(roles);


            user.setRolesString(roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            logger.info("Новый пользователь сохранен с ролями: {}", roles.stream().map(Role::getName).collect(Collectors.toList()));

            userService.save(user);

            return "redirect:/admin";
        } else {
            logger.warn("Попытка сохранить пользователя без ролей.");
            return "redirect:/admin";
        }
    }
}