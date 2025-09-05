package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class UserNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472956724834578234L;

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "USER_NOT_FOUND";
    }

    @Override
    public int getHttpStatusCode() {
        return 404;
    }
}
