package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class InvalidOrderStateException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -4857293847234958623L;

    public InvalidOrderStateException(String message) {
        super(message);
    }

    public InvalidOrderStateException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_ORDER_STATE";
    }

    @Override
    public int getHttpStatusCode() {
        return 409; // CONFLICT
    }
}
