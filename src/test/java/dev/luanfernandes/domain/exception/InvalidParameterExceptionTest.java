package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidParameterExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {

        String message = "Invalid parameter value";

        InvalidParameterException exception = new InvalidParameterException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_PARAMETER");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {

        String message = "Parameter validation failed";
        Throwable cause = new IllegalArgumentException("Negative value not allowed");

        InvalidParameterException exception = new InvalidParameterException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_PARAMETER");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    void shouldInheritFromBusinessException() {

        InvalidParameterException exception = new InvalidParameterException("Test message");

        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldHaveCorrectErrorCode() {

        InvalidParameterException exception = new InvalidParameterException("Test");

        String errorCode = exception.getErrorCode();

        assertThat(errorCode).isEqualTo("INVALID_PARAMETER");
    }

    @Test
    void shouldHaveCorrectHttpStatusCode() {

        InvalidParameterException exception = new InvalidParameterException("Test");

        int statusCode = exception.getHttpStatusCode();

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    void shouldHandleNullMessage() {

        InvalidParameterException exception = new InvalidParameterException(null);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_PARAMETER");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    void shouldHandleEmptyMessage() {

        String emptyMessage = "";

        InvalidParameterException exception = new InvalidParameterException(emptyMessage);

        assertThat(exception.getMessage()).isEqualTo(emptyMessage);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_PARAMETER");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    void shouldHandleNullCause() {

        String message = "Test message";

        InvalidParameterException exception = new InvalidParameterException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldMaintainCauseChain() {

        String rootMessage = "Root cause";
        String middleMessage = "Middle cause";
        String topMessage = "Top level message";

        RuntimeException rootCause = new RuntimeException(rootMessage);
        IllegalStateException middleCause = new IllegalStateException(middleMessage, rootCause);

        InvalidParameterException exception = new InvalidParameterException(topMessage, middleCause);

        assertThat(exception.getMessage()).isEqualTo(topMessage);
        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }
}
