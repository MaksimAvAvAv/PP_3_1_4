package org.example.service;

import org.example.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Optional<Role> findById(Long id); // Правильное объявление метода
}