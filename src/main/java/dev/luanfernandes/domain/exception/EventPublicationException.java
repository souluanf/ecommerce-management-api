package dev.luanfernandes.domain.exception;

import static java.lang.String.format;

import java.io.Serial;

public class EventPublicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1639504135005078807L;

    public EventPublicationException(String eventType, String entityId, String message, Throwable cause) {
        super(format("Failed to publish %s event for entity %s: %s", eventType, entityId, message), cause);
    }
}
