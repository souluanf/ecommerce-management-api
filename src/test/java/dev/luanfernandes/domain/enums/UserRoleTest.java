package dev.luanfernandes.domain.enums;

import static dev.luanfernandes.domain.enums.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void shouldHaveCorrectAuthoritiesForAdmin() {

        UserRole admin = UserRole.ADMIN;

        assertThat(admin.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(admin.getRoleName()).isEqualTo("ADMIN");
        assertThat(admin.getAuthorities()).isEqualTo(Set.of("ROLE_ADMIN"));
    }

    @Test
    void shouldHaveCorrectAuthoritiesForUser() {

        UserRole user = USER;

        assertThat(user.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(user.getRoleName()).isEqualTo(USER.name());
        assertThat(user.getAuthorities()).isEqualTo(Set.of("ROLE_USER"));
    }

    @Test
    void shouldCreateRoleFromAuthority() {

        UserRole adminFromAuthority = UserRole.fromAuthority("ROLE_ADMIN");
        UserRole userFromAuthority = UserRole.fromAuthority("ROLE_USER");

        assertThat(adminFromAuthority).isEqualTo(UserRole.ADMIN);
        assertThat(userFromAuthority).isEqualTo(USER);
    }

    @Test
    void shouldCreateRoleFromRoleName() {

        UserRole adminFromName = UserRole.fromRoleName("ADMIN");
        UserRole userFromName = UserRole.fromRoleName(USER.name());

        assertThat(adminFromName).isEqualTo(UserRole.ADMIN);
        assertThat(userFromName).isEqualTo(USER);
    }

    @Test
    void shouldThrowExceptionForInvalidAuthority() {

        assertThatThrownBy(() -> UserRole.fromAuthority("INVALID_ROLE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: INVALID_ROLE");
    }

    @Test
    void shouldThrowExceptionForInvalidRoleName() {

        assertThatThrownBy(() -> UserRole.fromRoleName("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: ROLE_INVALID");
    }

    @Test
    void shouldThrowExceptionForNullAuthority() {

        assertThatThrownBy(() -> UserRole.fromAuthority(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: null");
    }

    @Test
    void shouldThrowExceptionForNullRoleName() {

        assertThatThrownBy(() -> UserRole.fromRoleName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: ROLE_null");
    }

    @Test
    void shouldThrowExceptionForEmptyAuthority() {

        assertThatThrownBy(() -> UserRole.fromAuthority(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: ");
    }

    @Test
    void shouldThrowExceptionForEmptyRoleName() {

        assertThatThrownBy(() -> UserRole.fromRoleName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown authority: ROLE_");
    }

    @Test
    void shouldReturnCorrectRoleNameWithoutPrefix() {

        UserRole admin = UserRole.ADMIN;
        UserRole user = USER;

        String adminRoleName = admin.getRoleName();
        String userRoleName = user.getRoleName();

        assertThat(adminRoleName).isEqualTo("ADMIN");
        assertThat(userRoleName).isEqualTo(USER.name());
        assertThat(adminRoleName).doesNotStartWith("ROLE_");
        assertThat(userRoleName).doesNotStartWith("ROLE_");
    }

    @Test
    void shouldReturnImmutableAuthoritiesSet() {

        UserRole admin = UserRole.ADMIN;

        Set<String> authorities = admin.getAuthorities();

        assertThat(authorities).hasSize(1).contains("ROLE_ADMIN");

        assertThatThrownBy(() -> authorities.add("ANOTHER_ROLE")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldHaveExactlyTwoRoles() {

        UserRole[] roles = UserRole.values();

        assertThat(roles).hasSize(2).contains(UserRole.ADMIN, USER);
    }

    @Test
    void shouldHandleCaseSensitiveAuthority() {

        assertThatThrownBy(() -> UserRole.fromAuthority("role_admin")).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> UserRole.fromAuthority("ROLE_admin")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleCaseSensitiveRoleName() {
        assertThatThrownBy(() -> UserRole.fromRoleName("admin")).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> UserRole.fromRoleName("Admin")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldValidateEnumConsistency() {
        for (UserRole role : UserRole.values()) {
            UserRole reconstructed = UserRole.fromAuthority(role.getAuthority());
            assertThat(reconstructed).isEqualTo(role);
        }

        for (UserRole role : UserRole.values()) {
            UserRole reconstructed = UserRole.fromRoleName(role.getRoleName());
            assertThat(reconstructed).isEqualTo(role);
        }
    }
}
