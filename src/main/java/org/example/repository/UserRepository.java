package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по email
    User findByEmail(String email);

    // Поиск пользователя по email с возвратом Optional (безопаснее)
    Optional<User> findByEmailIgnoreCase(String email);

    // Проверка существования пользователя по email
    boolean existsByEmail(String email);

    // Поиск пользователей по имени
    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    // Поиск пользователей по фамилии
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    // Поиск пользователей по роли
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // Поиск пользователей с определенной ролью (по ID роли)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    // Получение всех пользователей с сортировкой по email
    @Query("SELECT u FROM User u ORDER BY u.email")
    List<User> findAllOrderByEmail();

    // Получение пользователей с пагинацией (очень полезно для больших списков)
    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findUsersWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    // Поиск пользователей по возрасту в диапазоне
    List<User> findByAgeBetween(int minAge, int maxAge);

    // Получение количества пользователей с определенной ролью
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Long countByRoleName(@Param("roleName") String roleName);
}