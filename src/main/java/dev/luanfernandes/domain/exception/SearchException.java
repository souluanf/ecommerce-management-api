package dev.luanfernandes.domain.exception;

import static java.lang.String.format;

import java.io.Serial;

public class SearchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1234567890123456789L;

    public SearchException(String operation, String productId, String message, Throwable cause) {
        super(format("Search operation '%s' failed for product %s: %s", operation, productId, message), cause);
    }

    public SearchException(String operation, String message, Throwable cause) {
        super(format("Search operation '%s' failed: %s", operation, message), cause);
    }
}
