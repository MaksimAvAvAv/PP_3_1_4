package org.example.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.service.UserService;
import org.example.service.RoleService; // Добавьте сервис для работы с ролями
import org.example.model.User;
import org.example.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class PageController {

    private final UserService userService;
    private final RoleService roleService; // Сервис для работы с ролями

    @Autowired
    public PageController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService; // Инициализация сервиса
    }

    // Страница админки с таблицей пользователей
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    // Страница пользователя
    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "user";
    }

    // Страница ошибки
    @GetMapping("/custom-error")
    public String customErrorPage() {
        return "error";
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll()); // Передаем все роли для редактирования
        return "edit-user"; // Возвращаем шаблон для редактирования пользователя
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
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    // =================== Методы для добавления нового пользователя ===================

    // 1. Отображение формы добавления нового пользователя
    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "add-user"; // Возвращаем шаблон для добавления пользователя
    }


    // 2. Обработка формы и сохранение нового пользователя
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("roleIds") List<Long> roleIds) {
        Set<Role> roles = roleIds.stream()
                .map(roleId -> {
                    Role role = new Role();
                    role.setId(roleId);
                    return role;
                }).collect(Collectors.toSet());

        user.setRoles(roles);
        userService.save(user);

        return "redirect:/admin";
    }
}