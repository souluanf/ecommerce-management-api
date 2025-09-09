package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class InvalidOrderStatusException extends BusinessException {

    private static final String ERROR_CODE = "INVALID_ORDER_STATUS";

    @Serial
    private static final long serialVersionUID = -5519827062408142522L;

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
        return 409;
    }
}
