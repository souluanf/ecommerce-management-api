package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for InvalidOrderStateException")
class InvalidOrderStateExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateException_WithMessage() {
        String message = "Order cannot be paid in current status: PAID";

        InvalidOrderStateException exception = new InvalidOrderStateException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateException_WithMessageAndCause() {
        String message = "Order state transition failed";
        Throwable cause = new RuntimeException("Database error");

        InvalidOrderStateException exception = new InvalidOrderStateException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOf_BusinessException() {
        InvalidOrderStateException exception = new InvalidOrderStateException("Test message");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle cancellation state errors")
    void shouldHandleCancellationStateErrors() {
        String message = "Order cannot be cancelled in current status: CANCELLED";

        InvalidOrderStateException exception = new InvalidOrderStateException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
    }

    @Test
    @DisplayName("Should handle payment state errors")
    void shouldHandlePaymentStateErrors() {
        String message = "Order cannot be paid in current status: CANCELLED";

        InvalidOrderStateException exception = new InvalidOrderStateException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
    }

    @Test
    @DisplayName("Should return correct HTTP status code for conflict")
    void shouldReturnCorrectHTTPStatusCode_ForConflict() {
        InvalidOrderStateException exception = new InvalidOrderStateException("Test message");

        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should maintain consistent error code")
    void shouldMaintainConsistent_ErrorCode() {
        InvalidOrderStateException exception1 = new InvalidOrderStateException("Message 1");
        InvalidOrderStateException exception2 = new InvalidOrderStateException("Message 2", new RuntimeException());

        assertThat(exception1.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
        assertThat(exception2.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
    }

    @Test
    @DisplayName("Should handle null cause gracefully")
    void shouldHandleNullCause_Gracefully() {
        String message = "Order state error";

        InvalidOrderStateException exception = new InvalidOrderStateException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATE");
    }
}
