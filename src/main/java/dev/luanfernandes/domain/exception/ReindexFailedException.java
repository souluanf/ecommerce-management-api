package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class ReindexFailedException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -7482937584729384729L;

    public ReindexFailedException(String message) {
        super(message);
    }

    public ReindexFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "REINDEX_FAILED";
    }

    @Override
    public int getHttpStatusCode() {
        return 500; // INTERNAL_SERVER_ERROR
    }
}
