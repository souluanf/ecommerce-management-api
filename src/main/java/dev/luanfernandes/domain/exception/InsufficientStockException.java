package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class InsufficientStockException extends BusinessException {

    private static final String ERROR_CODE = "INSUFFICIENT_STOCK";

    @Serial
    private static final long serialVersionUID = 8503277537971006476L;

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format(
                "Insufficient stock for product '%s'. Available: %d, Requested: %d",
                productName, available, requested));
    }

    public InsufficientStockException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 409;
    }
}
