package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for InvalidCredentialsException")
class InvalidCredentialsExceptionTest {

    @Test
    @DisplayName("Should create exception with default message")
    void shouldCreateException_WithDefaultMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should create exception with custom message")
    void shouldCreateException_WithCustomMessage() {
        String customMessage = "Authentication failed for user";

        InvalidCredentialsException exception = new InvalidCredentialsException(customMessage);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOf_BusinessException() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should return correct HTTP status code for unauthorized")
    void shouldReturnCorrectHTTPStatusCode_ForUnauthorized() {
        InvalidCredentialsException exception1 = new InvalidCredentialsException();
        InvalidCredentialsException exception2 = new InvalidCredentialsException("Custom message");

        assertThat(exception1.getHttpStatusCode()).isEqualTo(401);
        assertThat(exception2.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should maintain consistent error code")
    void shouldMaintainConsistent_ErrorCode() {
        InvalidCredentialsException exception1 = new InvalidCredentialsException();
        InvalidCredentialsException exception2 = new InvalidCredentialsException("Custom message");

        assertThat(exception1.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception2.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
    }

    @Test
    @DisplayName("Should handle null custom message")
    void shouldHandleNullCustomMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException(null);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should handle empty custom message")
    void shouldHandleEmptyCustomMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException("");

        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should handle various authentication failure scenarios")
    void shouldHandleVariousAuthenticationFailureScenarios() {
        InvalidCredentialsException wrongPassword = new InvalidCredentialsException("Wrong password provided");
        InvalidCredentialsException userNotFound =
                new InvalidCredentialsException("User not found with provided email");
        InvalidCredentialsException accountLocked = new InvalidCredentialsException("Account is locked");

        assertThat(wrongPassword.getMessage()).isEqualTo("Wrong password provided");
        assertThat(userNotFound.getMessage()).isEqualTo("User not found with provided email");
        assertThat(accountLocked.getMessage()).isEqualTo("Account is locked");

        assertThat(wrongPassword.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(userNotFound.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(accountLocked.getErrorCode()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("Should maintain security by providing generic error message")
    void shouldMaintainSecurity_ByProvidingGenericErrorMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
        assertThat(exception.getMessage()).doesNotContain("database");
        assertThat(exception.getMessage()).doesNotContain("username");
        assertThat(exception.getMessage()).doesNotContain("user not found");
    }
}
