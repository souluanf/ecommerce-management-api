package dev.luanfernandes.domain.exception;

/**
 * Exception thrown when attempting to create a user with an email that already exists.
 */
public class UserAlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
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
