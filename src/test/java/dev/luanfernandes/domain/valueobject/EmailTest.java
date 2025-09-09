package dev.luanfernandes.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Tests for Email")
class EmailTest {

    @ParameterizedTest
    @DisplayName("Should create email with valid formats")
    @MethodSource("validEmailProvider")
    void shouldCreateEmail_WithValidFormats(String validEmail, String description) {
        Email email = Email.of(validEmail);
        assertThat(email.value()).isEqualTo(validEmail);
    }

    private static Stream<Arguments> validEmailProvider() {
        return Stream.of(
                Arguments.of("test@example.com", "standard email"),
                Arguments.of("user@domain.com", "simple email"),
                Arguments.of("user@mail.example.com", "email with subdomain"),
                Arguments.of("user123@example.com", "email with numbers"),
                Arguments.of("user.name+tag@example.com", "email with special characters"),
                Arguments.of("user_name-test@example-domain.com", "email with underscores and hyphens"),
                Arguments.of("user@example.museum", "email with longer domain extension"),
                Arguments.of("a@b.co", "short valid email"),
                Arguments.of("very.long.email.address@very.long.domain.example", "long valid email"));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for invalid email formats")
    @MethodSource("invalidEmailProvider")
    void shouldThrowException_WhenEmailHasInvalidFormat(String invalidEmail, String description) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format: " + invalidEmail);
    }

    private static Stream<Arguments> invalidEmailProvider() {
        return Stream.of(
                Arguments.of("userexample.com", "missing @ symbol"),
                Arguments.of("user@", "missing domain"),
                Arguments.of("@example.com", "missing local part"),
                Arguments.of("user@domain", "no TLD"),
                Arguments.of("user@example.c", "TLD too short"),
                Arguments.of("user@example.toolongdomain", "TLD too long"),
                Arguments.of("user@@example.com", "multiple @ symbols"),
                Arguments.of("user@exam#ple.com", "special chars in domain"));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when email is null, empty or whitespace")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowException_WhenEmailIsNullEmptyOrWhitespace(String invalidInput) {
        assertThatThrownBy(() -> new Email(invalidInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or empty");
    }

    @Test
    @DisplayName("Should create email with constructor")
    void shouldCreateEmail_WithConstructor() {
        String validEmail = "test@example.com";
        Email email = new Email(validEmail);

        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("Should create email with factory method")
    void shouldCreateEmail_WithFactoryMethod() {
        String validEmail = "user@domain.com";
        Email email = Email.of(validEmail);

        assertThat(email.value()).isEqualTo(validEmail);
    }
}
