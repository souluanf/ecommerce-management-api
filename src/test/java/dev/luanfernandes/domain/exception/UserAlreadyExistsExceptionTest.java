package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create exception with email")
    void shouldCreateException_WithEmail() {
        String email = "test@example.com";

        UserAlreadyExistsException exception = new UserAlreadyExistsException(email);

        String expectedMessage = "User already exists with email: " + email;
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOf_BusinessException() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("test@example.com");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle different email formats")
    void shouldHandleDifferentEmailFormats() {
        String email1 = "user@domain.com";
        String email2 = "user.name+tag@example.org";
        String email3 = "user123@subdomain.example.com";

        UserAlreadyExistsException exception1 = new UserAlreadyExistsException(email1);
        UserAlreadyExistsException exception2 = new UserAlreadyExistsException(email2);
        UserAlreadyExistsException exception3 = new UserAlreadyExistsException(email3);

        assertThat(exception1.getMessage()).isEqualTo("User already exists with email: " + email1);
        assertThat(exception2.getMessage()).isEqualTo("User already exists with email: " + email2);
        assertThat(exception3.getMessage()).isEqualTo("User already exists with email: " + email3);
    }

    @Test
    @DisplayName("Should return correct HTTP status code for conflict")
    void shouldReturnCorrectHTTPStatusCode_ForConflict() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("test@example.com");

        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should maintain consistent error code")
    void shouldMaintainConsistent_ErrorCode() {
        UserAlreadyExistsException exception1 = new UserAlreadyExistsException("user1@example.com");
        UserAlreadyExistsException exception2 = new UserAlreadyExistsException("user2@example.com");

        assertThat(exception1.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(exception2.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
    }

    @Test
    @DisplayName("Should handle empty email string")
    void shouldHandleEmptyEmailString() {
        String emptyEmail = "";

        UserAlreadyExistsException exception = new UserAlreadyExistsException(emptyEmail);

        assertThat(exception.getMessage()).isEqualTo("User already exists with email: ");
        assertThat(exception.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Should handle null email")
    void shouldHandleNullEmail() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException(null);

        assertThat(exception.getMessage()).isEqualTo("User already exists with email: null");
        assertThat(exception.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Should handle email with special characters")
    void shouldHandleEmail_WithSpecialCharacters() {
        String specialEmail = "user+special@domain.com";

        UserAlreadyExistsException exception = new UserAlreadyExistsException(specialEmail);

        assertThat(exception.getMessage()).isEqualTo("User already exists with email: " + specialEmail);
        assertThat(exception.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
    }
}
