package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithEmail() {

        String email = "user@example.com";

        UserNotFoundException exception = new UserNotFoundException(email);

        assertThat(exception.getMessage()).isEqualTo("User not found with email: " + email);
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {

        String message = "Database query failed";
        Throwable cause = new RuntimeException("Connection timeout");

        UserNotFoundException exception = new UserNotFoundException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleNullEmail() {

        String nullEmail = null;

        UserNotFoundException exception = new UserNotFoundException(nullEmail);

        assertThat(exception.getMessage()).isEqualTo("User not found with email: null");
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleEmptyEmail() {

        String emptyEmail = "";

        UserNotFoundException exception = new UserNotFoundException(emptyEmail);

        assertThat(exception.getMessage()).isEqualTo("User not found with email: ");
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {

        String specialEmail = "user+tag@sub.domain.com";

        UserNotFoundException exception = new UserNotFoundException(specialEmail);

        assertThat(exception.getMessage()).isEqualTo("User not found with email: " + specialEmail);
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleNullCause() {

        String message = "Custom message";

        UserNotFoundException exception = new UserNotFoundException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }
}
