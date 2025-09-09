package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidOrderStatusExceptionTest {

    @Test
    void shouldCreateException_WithCurrentAndExpectedStatus() {
        String currentStatus = "PAID";
        String expectedStatus = "PENDING";

        InvalidOrderStatusException exception = new InvalidOrderStatusException(currentStatus, expectedStatus);

        assertThat(exception.getMessage())
                .isEqualTo("Cannot perform operation. Order status is 'PAID', expected 'PENDING'");
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATUS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    void shouldCreateException_WithCustomMessage() {
        String customMessage = "Custom order status error message";

        InvalidOrderStatusException exception = new InvalidOrderStatusException(customMessage);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATUS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    void shouldHaveCorrectErrorCode() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("CANCELLED", "PENDING");

        assertThat(exception.getErrorCode()).isEqualTo("INVALID_ORDER_STATUS");
    }

    @Test
    void shouldHaveConflictHttpStatusCode() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("CANCELLED", "PENDING");

        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    void shouldHandleAllOrderStatusTypes() {
        InvalidOrderStatusException exception1 = new InvalidOrderStatusException("PENDING", "PAID");
        InvalidOrderStatusException exception2 = new InvalidOrderStatusException("CANCELLED", "PENDING");
        InvalidOrderStatusException exception3 = new InvalidOrderStatusException("PAID", "CANCELLED");

        assertThat(exception1.getMessage()).contains("PENDING").contains("PAID");
        assertThat(exception2.getMessage()).contains("CANCELLED").contains("PENDING");
        assertThat(exception3.getMessage()).contains("PAID").contains("CANCELLED");
    }

    @Test
    void shouldInheritFromBusinessException() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("PAID", "PENDING");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldMaintainConsistency_AcrossDifferentConstructors() {
        InvalidOrderStatusException exception1 = new InvalidOrderStatusException("PAID", "PENDING");
        InvalidOrderStatusException exception2 = new InvalidOrderStatusException("Custom message");

        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
        assertThat(exception1.getHttpStatusCode()).isEqualTo(exception2.getHttpStatusCode());
    }

    @Test
    void shouldHandleEdgeCase_WithEmptyStatusValues() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("", "PENDING");

        assertThat(exception.getMessage())
                .isEqualTo("Cannot perform operation. Order status is '', expected 'PENDING'");
    }

    @Test
    void shouldHandleEdgeCase_WithSpecialCharactersInStatus() {
        String currentStatus = "CUSTOM_STATUS_123";
        String expectedStatus = "ANOTHER-STATUS-456";

        InvalidOrderStatusException exception = new InvalidOrderStatusException(currentStatus, expectedStatus);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot perform operation. Order status is 'CUSTOM_STATUS_123', expected 'ANOTHER-STATUS-456'");
    }
}
