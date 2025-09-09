package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class InvalidCredentialsException extends BusinessException {

    private static final String ERROR_CODE = "INVALID_CREDENTIALS";

    @Serial
    private static final long serialVersionUID = -3672728830486993911L;

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 401;
    }
}
