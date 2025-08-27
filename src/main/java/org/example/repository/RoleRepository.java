package org.example.repository;

import org.example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findByIdIn(List<Long> ids);

    List<Role> findByNameIn(List<String> names);

    boolean existsByName(String name);

    @Query("SELECT r FROM Role r ORDER BY r.name")
    List<Role> findAllOrderByName();

    List<Role> findByNameContainingIgnoreCase(String namePart);

    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);
}