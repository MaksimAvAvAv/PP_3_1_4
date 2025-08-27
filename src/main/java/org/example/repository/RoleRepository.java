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

    // Поиск роли по имени
    Role findByName(String name);

    // Поиск роли по имени с возвратом Optional (безопаснее)
    Optional<Role> findByNameIgnoreCase(String name);

    // Поиск нескольких ролей по ID
    List<Role> findByIdIn(List<Long> ids);

    // Поиск нескольких ролей по именам
    List<Role> findByNameIn(List<String> names);

    // Проверка существования роли по имени
    boolean existsByName(String name);

    // Получение всех ролей с сортировкой по имени
    @Query("SELECT r FROM Role r ORDER BY r.name")
    List<Role> findAllOrderByName();

    // Поиск ролей по части имени (для поиска)
    List<Role> findByNameContainingIgnoreCase(String namePart);

    // Получение ролей для пользователя (если понадобится)
    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);
}