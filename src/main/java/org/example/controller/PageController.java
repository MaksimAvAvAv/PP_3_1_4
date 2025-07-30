package org.example.controller;

import java.security.Principal;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public PageController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin";
    }

    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "user";
    }

    @GetMapping("/custom-error")
    public String customErrorPage() {
        return "error";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);

        List<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);

        // Логируем количество ролей и их имена
        logger.info("Передано {} ролей в модель для редактирования пользователя с ID {}.", roles.size(), id);
        for (Role role : roles) {
            logger.info("Роль: {}", role.getName());
        }

        List<String> userRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        model.addAttribute("userRoles", userRoles);

        return "edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam("roleIds") List<Long> roleIds) {

        Set<Role> roles = roleIds.stream()
                .map(roleId -> {
                    Role role = new Role();
                    role.setId(roleId);
                    return role;
                }).collect(Collectors.toSet());

        user.setRoles(roles);

        // Сохраняем обновленного пользователя
        userService.updateUser(user);

        logger.info("Пользователь с ID {} обновлен с ролями: {}", user.getId(), roles.stream().map(Role::getName).collect(Collectors.toList()));

        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);

        logger.info("Пользователь с ID {} был удален.", id);

        return "redirect:/admin";
    }

    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<Role> roles = roleService.findAll();
        model.addAttribute("allRoles", roles);

        logger.info("Передано {} ролей в модель для добавления нового пользователя.", roles.size());

        return "add-user";
    }

    @PostMapping("/users/save")
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
            logger.info("Новый пользователь сохранен с ролями: {}", roles.stream().map(Role::getName).collect(Collectors.toList()));

            // Сохраняем нового пользователя
            userService.save(user);

            return "redirect:/admin";
        } else {
            logger.warn("Попытка сохранить пользователя без ролей.");
            return "redirect:/admin"; // Можно добавить сообщение об ошибке
        }
    }
}