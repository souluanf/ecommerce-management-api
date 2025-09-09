package dev.luanfernandes.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {

        String email = "user@example.com";
        String password = "encodedPassword";

        UserDomain user = new UserDomain(
                UserId.of(UUID.randomUUID()), new Email(email), password, UserRole.USER, LocalDateTime.now());

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_USER");
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void shouldLoadUserWithAdminRole() {

        String email = "admin@example.com";
        String password = "adminPassword";

        UserDomain adminUser = new UserDomain(
                UserId.of(UUID.randomUUID()), new Email(email), password, UserRole.ADMIN, LocalDateTime.now());

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotExists() {

        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenInvalidEmail() {

        String invalidEmail = "invalid-email";

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(invalidEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + invalidEmail);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenRepositoryThrowsException() {

        String email = "user@example.com";

        when(userRepository.findByEmail(any(Email.class))).thenThrow(new RuntimeException("Database connection error"));

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email)
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldLoadUserWithEmptyName() {

        String email = "user@example.com";

        UserDomain user = new UserDomain(
                UserId.of(UUID.randomUUID()), new Email(email), "password", UserRole.USER, LocalDateTime.now());

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_USER");
    }
}
