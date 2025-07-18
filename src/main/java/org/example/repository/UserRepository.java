package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}