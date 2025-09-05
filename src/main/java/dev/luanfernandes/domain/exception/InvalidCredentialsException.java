package dev.luanfernandes.domain.exception;

/**
 * Exception thrown when user provides invalid login credentials.
 */
public class InvalidCredentialsException extends BusinessException {

    private static final String ERROR_CODE = "INVALID_CREDENTIALS";

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
        return 401; // UNAUTHORIZED
    }
}
