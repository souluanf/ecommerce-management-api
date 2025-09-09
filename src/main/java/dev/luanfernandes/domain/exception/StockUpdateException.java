package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class StockUpdateException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8917942874102001947L;

    public StockUpdateException(String productId, Throwable cause) {
        super("Failed to update stock for product: " + productId);
        initCause(cause);
    }

    @Override
    public String getErrorCode() {
        return "STOCK_UPDATE_FAILED";
    }

    @Override
    public int getHttpStatusCode() {
        return 500;
    }
}
