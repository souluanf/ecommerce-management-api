package dev.luanfernandes.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for UserDomain")
class UserDomainTest {

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUser_WithValidData() {
        UserId id = UserId.generate();
        Email email = Email.of("test@example.com");
        String password = "encodedPassword";
        UserRole role = UserRole.USER;
        LocalDateTime createdAt = LocalDateTime.now();

        UserDomain user = new UserDomain(id, email, password, role, createdAt);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getUpdatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Should identify admin user correctly")
    void shouldIdentifyAdminUser_Correctly() {
        UserDomain adminUser = createUser(UserRole.ADMIN);
        UserDomain regularUser = createUser(UserRole.USER);

        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(regularUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should update timestamp when updateTimestamp is called")
    void shouldUpdateTimestamp_WhenUpdateTimestampIsCalled() {
        UserDomain user = createUser(UserRole.USER);
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        user.updateTimestamp();

        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should maintain created timestamp after update")
    void shouldMaintainCreatedTimestamp_AfterUpdate() {
        UserDomain user = createUser(UserRole.USER);
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        user.updateTimestamp();

        assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should handle different user roles")
    void shouldHandleDifferentUserRoles() {
        UserDomain userRole = createUser(UserRole.USER);
        UserDomain adminRole = createUser(UserRole.ADMIN);

        assertThat(userRole.getRole()).isEqualTo(UserRole.USER);
        assertThat(adminRole.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(userRole.isAdmin()).isFalse();
        assertThat(adminRole.isAdmin()).isTrue();
    }

    private UserDomain createUser(UserRole role) {
        return new UserDomain(
                UserId.generate(), Email.of("test@example.com"), "encodedPassword", role, LocalDateTime.now());
    }
}
