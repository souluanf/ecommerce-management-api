package dev.luanfernandes.domain.entity;

import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;

public class UserDomain {

    private final UserId id;
    private final Email email;
    private final String password;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDomain(UserId id, Email email, String password, UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
