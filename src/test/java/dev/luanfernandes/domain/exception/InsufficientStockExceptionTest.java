package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for InsufficientStockException")
class InsufficientStockExceptionTest {

    @Test
    @DisplayName("Should create exception with product details")
    void shouldCreateException_WithProductDetails() {
        String productName = "Test Product";
        int available = 5;
        int requested = 10;

        InsufficientStockException exception = new InsufficientStockException(productName, available, requested);

        String expectedMessage = String.format(
                "Insufficient stock for product '%s'. Available: %d, Requested: %d", productName, available, requested);
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should create exception with custom message")
    void shouldCreateException_WithCustomMessage() {
        String customMessage = "Custom insufficient stock message";

        InsufficientStockException exception = new InsufficientStockException(customMessage);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOf_BusinessException() {
        InsufficientStockException exception = new InsufficientStockException("Test message");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle zero stock scenario")
    void shouldHandleZeroStockScenario() {
        String productName = "Out of Stock Product";
        int available = 0;
        int requested = 1;

        InsufficientStockException exception = new InsufficientStockException(productName, available, requested);

        String expectedMessage = "Insufficient stock for product 'Out of Stock Product'. Available: 0, Requested: 1";
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should handle large quantity requests")
    void shouldHandleLargeQuantityRequests() {
        String productName = "Limited Product";
        int available = 100;
        int requested = 1000;

        InsufficientStockException exception = new InsufficientStockException(productName, available, requested);

        String expectedMessage = "Insufficient stock for product 'Limited Product'. Available: 100, Requested: 1000";
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should handle products with special characters in name")
    void shouldHandleProducts_WithSpecialCharactersInName() {
        String productName = "Product with symbols @#$%";
        int available = 2;
        int requested = 5;

        InsufficientStockException exception = new InsufficientStockException(productName, available, requested);

        String expectedMessage =
                "Insufficient stock for product 'Product with symbols @#$%'. Available: 2, Requested: 5";
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should maintain error code consistency")
    void shouldMaintainErrorCode_Consistency() {
        InsufficientStockException exception1 = new InsufficientStockException("Product A", 1, 2);
        InsufficientStockException exception2 = new InsufficientStockException("Custom message");

        assertThat(exception1.getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
        assertThat(exception2.getErrorCode()).isEqualTo("INSUFFICIENT_STOCK");
        assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode());
    }

    @Test
    @DisplayName("Should maintain HTTP status code consistency")
    void shouldMaintainHTTPStatusCode_Consistency() {
        InsufficientStockException exception1 = new InsufficientStockException("Product A", 1, 2);
        InsufficientStockException exception2 = new InsufficientStockException("Custom message");

        assertThat(exception1.getHttpStatusCode()).isEqualTo(409);
        assertThat(exception2.getHttpStatusCode()).isEqualTo(409);
        assertThat(exception1.getHttpStatusCode()).isEqualTo(exception2.getHttpStatusCode());
    }
}
