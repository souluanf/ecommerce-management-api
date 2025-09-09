package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StockUpdateExceptionTest {

    @Test
    void shouldCreateExceptionWithProductIdAndCause() {

        String productId = "product-789";
        Throwable cause = new RuntimeException("Database connection failed");

        StockUpdateException exception = new StockUpdateException(productId, cause);

        assertThat(exception.getMessage()).isEqualTo("Failed to update stock for product: " + productId);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("STOCK_UPDATE_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldHandleNullProductId() {

        String nullId = null;
        Throwable cause = new IllegalStateException("Invalid state");

        StockUpdateException exception = new StockUpdateException(nullId, cause);

        assertThat(exception.getMessage()).isEqualTo("Failed to update stock for product: null");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("STOCK_UPDATE_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void shouldHandleEmptyProductId() {

        String emptyId = "";
        Throwable cause = new RuntimeException("Network timeout");

        StockUpdateException exception = new StockUpdateException(emptyId, cause);

        assertThat(exception.getMessage()).isEqualTo("Failed to update stock for product: ");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("STOCK_UPDATE_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void shouldHandleNullCause() {

        String productId = "product-999";
        Throwable nullCause = null;

        StockUpdateException exception = new StockUpdateException(productId, nullCause);

        assertThat(exception.getMessage()).isEqualTo("Failed to update stock for product: " + productId);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("STOCK_UPDATE_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void shouldMaintainCauseChain() {

        String productId = "product-chain";
        RuntimeException rootCause = new RuntimeException("Root cause");
        IllegalStateException middleCause = new IllegalStateException("Middle cause", rootCause);

        StockUpdateException exception = new StockUpdateException(productId, middleCause);

        assertThat(exception.getMessage()).isEqualTo("Failed to update stock for product: " + productId);
        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    void shouldHandleUUIDProductId() {

        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        Throwable cause = new RuntimeException("Stock update failed");

        StockUpdateException exception = new StockUpdateException(uuidId, cause);

        assertThat(exception.getMessage()).contains(uuidId);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("STOCK_UPDATE_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }
}
