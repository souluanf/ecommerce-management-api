package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class InvalidParameterException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -2847392845734829463L;

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_PARAMETER";
    }

    @Override
    public int getHttpStatusCode() {
        return 400;
    }
}
