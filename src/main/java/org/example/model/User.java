package org.example.model;

import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private int age;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "roles_string")
    private String rolesString;

    // Конструкторы
    public User() {
    }

    public User(Long id, String firstName, String lastName, int age, String email, String password, Set<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        this.setRoles(roles); // Используем сеттер для consistency
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
        // Автоматически обновляем строковое представление ролей
        if (roles != null && !roles.isEmpty()) {
            this.rolesString = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
        } else {
            this.rolesString = "";
        }
    }

    public String getRolesString() {
        return rolesString;
    }

    public void setRolesString(String rolesString) {
        this.rolesString = rolesString;
    }

    // Метод для удобного получения имен ролей (без аннотации @Transient)
    public java.util.List<String> getRoleNames() {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Важные методы для корректной работы
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}