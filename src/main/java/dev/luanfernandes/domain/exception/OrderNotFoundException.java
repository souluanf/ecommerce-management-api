package dev.luanfernandes.domain.exception;

import java.io.Serial;
import java.util.UUID;

public class OrderNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "ORDER_NOT_FOUND";

    @Serial
    private static final long serialVersionUID = -6389610667825832030L;

    public OrderNotFoundException(String orderId) {
        super("Order not found with ID: " + orderId);
    }

    public OrderNotFoundException(UUID orderId) {
        super("Order not found with ID: " + orderId);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 404;
    }
}
