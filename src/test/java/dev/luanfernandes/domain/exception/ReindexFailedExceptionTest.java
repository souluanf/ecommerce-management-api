package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReindexFailedExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {

        String message = "Failed to reindex products";

        ReindexFailedException exception = new ReindexFailedException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("REINDEX_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {

        String message = "Elasticsearch connection failed during reindex";
        Throwable cause = new RuntimeException("Connection timeout");

        ReindexFailedException exception = new ReindexFailedException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("REINDEX_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void shouldHandleNullMessage() {

        ReindexFailedException exception = new ReindexFailedException(null);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("REINDEX_FAILED");
        assertThat(exception.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void shouldHandleNullCause() {

        String message = "Test reindex failure";

        ReindexFailedException exception = new ReindexFailedException(message, null);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldMaintainCauseChain() {

        String message = "Reindex operation failed";
        RuntimeException rootCause = new RuntimeException("Database connection lost");
        IllegalStateException middleCause = new IllegalStateException("Index corrupted", rootCause);

        ReindexFailedException exception = new ReindexFailedException(message, middleCause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }
}
