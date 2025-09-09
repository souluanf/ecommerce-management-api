package dev.luanfernandes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for SearchException")
class SearchExceptionTest {

    @Test
    @DisplayName("Should create exception with operation, product ID and message")
    void shouldCreateException_WithOperationProductIdAndMessage() {
        String operation = "index";
        String productId = "prod-123";
        String message = "Connection timeout";
        Throwable cause = new RuntimeException("Network error");

        SearchException exception = new SearchException(operation, productId, message, cause);

        assertThat(exception.getMessage())
                .isEqualTo("Search operation 'index' failed for product prod-123: Connection timeout");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should create exception with operation and message only")
    void shouldCreateException_WithOperationAndMessageOnly() {
        String operation = "search";
        String message = "Invalid query parameters";
        Throwable cause = new IllegalArgumentException("Query is null");

        SearchException exception = new SearchException(operation, message, cause);

        assertThat(exception.getMessage()).isEqualTo("Search operation 'search' failed: Invalid query parameters");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
