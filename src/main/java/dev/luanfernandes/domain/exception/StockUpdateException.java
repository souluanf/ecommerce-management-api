package dev.luanfernandes.domain.exception;

public class StockUpdateException extends BusinessException {

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
