package dev.luanfernandes.domain.exception;

/**
 * Exception thrown when trying to order more items than available in stock.
 */
public class InsufficientStockException extends BusinessException {

    private static final String ERROR_CODE = "INSUFFICIENT_STOCK";

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
        return 409; // CONFLICT
    }
}
