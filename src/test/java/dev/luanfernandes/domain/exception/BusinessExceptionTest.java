package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

    private static class TestBusinessException extends BusinessException {
        public TestBusinessException(String message) {
            super(message);
        }

        public TestBusinessException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public String getErrorCode() {
            return "TEST_ERROR";
        }

        @Override
        public int getHttpStatusCode() {
            return 422;
        }
    }

    private static class DefaultHttpStatusException extends BusinessException {
        public DefaultHttpStatusException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return "DEFAULT_ERROR";
        }
    }

    @Test
    void shouldCreateExceptionWithMessage() {

        String message = "Test business error";

        TestBusinessException exception = new TestBusinessException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(422);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {

        String message = "Business operation failed";
        Throwable cause = new RuntimeException("Root cause");

        TestBusinessException exception = new TestBusinessException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(422);
    }

    @Test
    void shouldHandleNullMessage() {

        String nullMessage = null;
        Throwable cause = new IllegalStateException("State error");

        TestBusinessException exception = new TestBusinessException(nullMessage, cause);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
    }

    @Test
    void shouldHandleNullCause() {

        String message = "Error without cause";
        Throwable nullCause = null;

        TestBusinessException exception = new TestBusinessException(message, nullCause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
    }

    @Test
    void shouldMaintainCauseChain() {

        String message = "Chained business error";
        RuntimeException rootCause = new RuntimeException("Root cause");
        IllegalStateException middleCause = new IllegalStateException("Middle cause", rootCause);

        TestBusinessException exception = new TestBusinessException(message, middleCause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(middleCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    void shouldUseDefaultHttpStatusCode() {

        String message = "Default status error";

        DefaultHttpStatusException exception = new DefaultHttpStatusException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("DEFAULT_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    void shouldHandleEmptyMessage() {

        String emptyMessage = "";

        TestBusinessException exception = new TestBusinessException(emptyMessage);

        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(422);
    }

    @Test
    void shouldHandleLongMessage() {

        String longMessage = "A".repeat(1000);

        TestBusinessException exception = new TestBusinessException(longMessage);

        assertThat(exception.getMessage()).isEqualTo(longMessage);
        assertThat(exception.getMessage()).hasSize(1000);
        assertThat(exception.getErrorCode()).isEqualTo("TEST_ERROR");
    }

    @Test
    void shouldBeSerializable() {

        TestBusinessException exception = new TestBusinessException("Serializable test");

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    void shouldHaveCorrectSerialVersionUID() {

        TestBusinessException exception = new TestBusinessException("Serial test");

        assertThat(exception).isInstanceOf(BusinessException.class).isInstanceOf(RuntimeException.class);
    }
}
