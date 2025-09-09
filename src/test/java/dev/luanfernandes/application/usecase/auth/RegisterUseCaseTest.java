package dev.luanfernandes.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.RegisterRequest;
import dev.luanfernandes.domain.dto.UserResponse;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.exception.UserAlreadyExistsException;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for RegisterUseCase")
class RegisterUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUser_WhenEmailDoesNotExist() {
        String email = "newuser@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        UserRole role = UserRole.USER;

        RegisterRequest request = new RegisterRequest(email, password, role);

        UserDomain savedUser = createUser(email, encodedPassword, role);

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        UserResponse response = registerUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(savedUser.getId().value());
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.role()).isEqualTo(role);
        assertThat(response.createdAt()).isEqualTo(savedUser.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(savedUser.getUpdatedAt());

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowException_WhenEmailAlreadyExists() {
        String email = "existing@example.com";
        String password = "password123";
        UserRole role = UserRole.USER;

        RegisterRequest request = new RegisterRequest(email, password, role);

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> registerUseCase.execute(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(email);

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Should register admin user successfully")
    void shouldRegisterAdminUser_WhenEmailDoesNotExist() {
        String email = "admin@example.com";
        String password = "adminpass";
        String encodedPassword = "encodedAdminPass";
        UserRole role = UserRole.ADMIN;

        RegisterRequest request = new RegisterRequest(email, password, role);

        UserDomain savedUser = createUser(email, encodedPassword, role);

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        UserResponse response = registerUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.id()).isEqualTo(savedUser.getId().value());
        assertThat(response.createdAt()).isEqualTo(savedUser.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(savedUser.getUpdatedAt());

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void shouldEncodePassword_BeforeSavingUser() {
        String email = "secure@example.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "hashedPassword";
        UserRole role = UserRole.USER;

        RegisterRequest request = new RegisterRequest(email, rawPassword, role);

        UserDomain savedUser = createUser(email, encodedPassword, role);

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        registerUseCase.execute(request);

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(UserDomain.class));
    }

    private UserDomain createUser(String email, String password, UserRole role) {
        return new UserDomain(UserId.generate(), new Email(email), password, role, LocalDateTime.now());
    }
}
