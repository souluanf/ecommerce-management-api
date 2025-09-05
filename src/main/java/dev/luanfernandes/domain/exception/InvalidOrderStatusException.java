package dev.luanfernandes.domain.exception;

/**
 * Exception thrown when trying to perform an operation on an order with invalid status.
 */
public class InvalidOrderStatusException extends BusinessException {

    private static final String ERROR_CODE = "INVALID_ORDER_STATUS";

    public InvalidOrderStatusException(String currentStatus, String expectedStatus) {
        super(String.format(
                "Cannot perform operation. Order status is '%s', expected '%s'", currentStatus, expectedStatus));
    }

    public InvalidOrderStatusException(String message) {
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
