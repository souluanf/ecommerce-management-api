package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductInUseExceptionTest {

    @Test
    void shouldCreateExceptionWithProductId() {

        String productId = "product-123";

        ProductInUseException exception = new ProductInUseException(productId);

        assertThat(exception.getMessage())
                .isEqualTo("Cannot delete product that is associated with existing orders: " + productId);
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_IN_USE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldHandleNullProductId() {

        String nullId = null;

        ProductInUseException exception = new ProductInUseException(nullId);

        assertThat(exception.getMessage())
                .isEqualTo("Cannot delete product that is associated with existing orders: null");
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_IN_USE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    void shouldHandleEmptyProductId() {

        String emptyId = "";

        ProductInUseException exception = new ProductInUseException(emptyId);

        assertThat(exception.getMessage()).isEqualTo("Cannot delete product that is associated with existing orders: ");
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_IN_USE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    void shouldHandleUUIDProductId() {

        String uuidId = "550e8400-e29b-41d4-a716-446655440000";

        ProductInUseException exception = new ProductInUseException(uuidId);

        assertThat(exception.getMessage()).contains(uuidId);
        assertThat(exception.getErrorCode()).isEqualTo("PRODUCT_IN_USE");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
    }
}
