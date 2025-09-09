package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithProductId() {

        String productId = "product-456";

        ProductNotFoundException exception = new ProductNotFoundException(productId);

        assertThat(exception.getMessage()).isEqualTo("Product not found with ID: " + productId);
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldHandleNullProductId() {

        String nullId = null;

        ProductNotFoundException exception = new ProductNotFoundException(nullId);

        assertThat(exception.getMessage()).isEqualTo("Product not found with ID: null");
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleEmptyProductId() {

        String emptyId = "";

        ProductNotFoundException exception = new ProductNotFoundException(emptyId);

        assertThat(exception.getMessage()).isEqualTo("Product not found with ID: ");
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleUUIDProductId() {

        String uuidId = "550e8400-e29b-41d4-a716-446655440000";

        ProductNotFoundException exception = new ProductNotFoundException(uuidId);

        assertThat(exception.getMessage()).contains(uuidId);
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHaveCorrectErrorCodeConstant() {

        ProductNotFoundException exception = new ProductNotFoundException("test-id");

        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_NOT_FOUND");
    }
}
