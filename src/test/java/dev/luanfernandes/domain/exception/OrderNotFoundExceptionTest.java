package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithStringId() {

        String orderId = "order-123";

        OrderNotFoundException exception = new OrderNotFoundException(orderId);

        assertThat(exception.getMessage()).isEqualTo("Order not found with ID: " + orderId);
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldCreateExceptionWithUUID() {

        UUID orderId = UUID.randomUUID();

        OrderNotFoundException exception = new OrderNotFoundException(orderId);

        assertThat(exception.getMessage()).isEqualTo("Order not found with ID: " + orderId);
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldHandleNullStringId() {

        String nullId = null;

        OrderNotFoundException exception = new OrderNotFoundException(nullId);

        assertThat(exception.getMessage()).isEqualTo("Order not found with ID: null");
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleNullUUID() {

        UUID nullId = null;

        OrderNotFoundException exception = new OrderNotFoundException(nullId);

        assertThat(exception.getMessage()).isEqualTo("Order not found with ID: null");
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldHandleEmptyStringId() {

        String emptyId = "";

        OrderNotFoundException exception = new OrderNotFoundException(emptyId);

        assertThat(exception.getMessage()).isEqualTo("Order not found with ID: ");
        assertThat(exception.getErrorCode()).isEqualTo("ORDER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }
}
