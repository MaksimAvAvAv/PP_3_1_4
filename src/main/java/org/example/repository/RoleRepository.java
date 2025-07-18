package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}