package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidTokenExceptionTest {

    @Test
    void shouldCreateException_WithMessage() {
        String message = "Invalid JWT token";

        InvalidTokenException exception = new InvalidTokenException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    void shouldCreateException_WithMessageAndCause() {
        String message = "Token validation failed";
        RuntimeException cause = new RuntimeException("JWT signature invalid");

        InvalidTokenException exception = new InvalidTokenException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    void shouldHaveCorrectErrorCode() {
        InvalidTokenException exception = new InvalidTokenException("test message");

        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
    }

    @Test
    void shouldHaveUnauthorizedHttpStatusCode() {
        InvalidTokenException exception = new InvalidTokenException("test message");

        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    void shouldInheritFromBusinessException() {
        InvalidTokenException exception = new InvalidTokenException("test message");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldMaintainConsistency_AcrossDifferentConstructors() {
        InvalidTokenException exception1 = new InvalidTokenException("message1");
        InvalidTokenException exception2 = new InvalidTokenException("message2", new RuntimeException());

        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
        assertThat(exception1.getHttpStatusCode()).isEqualTo(exception2.getHttpStatusCode());
    }

    @Test
    void shouldHandleNullCause() {
        String message = "Invalid token format";

        InvalidTokenException exception = new InvalidTokenException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
    }
}
