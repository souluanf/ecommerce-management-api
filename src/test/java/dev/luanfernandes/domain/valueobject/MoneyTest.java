package dev.luanfernandes.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for Money")
class MoneyTest {

    @Test
    @DisplayName("Should create money with valid BigDecimal value")
    void shouldCreateMoney_WithValidBigDecimalValue() {
        BigDecimal value = new BigDecimal("99.99");
        Money money = new Money(value);

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("Should create money with factory method using BigDecimal")
    void shouldCreateMoney_WithFactoryMethodUsingBigDecimal() {
        Money money = Money.of(new BigDecimal("50.00"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Should create money with factory method using double")
    void shouldCreateMoney_WithFactoryMethodUsingDouble() {
        Money money = Money.of(25.50);

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    @DisplayName("Should create zero money")
    void shouldCreateZeroMoney() {
        Money money = Money.zero();

        assertThat(money.value()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(money.isZero()).isTrue();
    }

    @Test
    @DisplayName("Should round value to 2 decimal places")
    void shouldRoundValue_To2DecimalPlaces() {
        Money money = new Money(new BigDecimal("99.999"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should round value down when needed")
    void shouldRoundValueDown_WhenNeeded() {
        Money money = new Money(new BigDecimal("99.994"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowException_WhenValueIsNull() {
        assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Money value cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when value is negative")
    void shouldThrowException_WhenValueIsNegative() {
        BigDecimal negativeValue = new BigDecimal("-10.00");

        assertThatThrownBy(() -> new Money(negativeValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Money value cannot be negative: " + negativeValue);
    }

    @Test
    @DisplayName("Should add two money values correctly")
    void shouldAddTwoMoneyValues_Correctly() {
        Money money1 = new Money(new BigDecimal("25.50"));
        Money money2 = new Money(new BigDecimal("10.25"));

        Money result = money1.add(money2);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Should add zero to money value")
    void shouldAddZero_ToMoneyValue() {
        Money money = new Money(new BigDecimal("100.00"));
        Money zero = Money.zero();

        Money result = money.add(zero);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should multiply money by quantity correctly")
    void shouldMultiplyMoney_ByQuantityCorrectly() {
        Money money = new Money(new BigDecimal("25.50"));

        Money result = money.multiply(3);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("76.50"));
    }

    @Test
    @DisplayName("Should multiply money by zero")
    void shouldMultiplyMoney_ByZero() {
        Money money = new Money(new BigDecimal("100.00"));

        Money result = money.multiply(0);

        assertThat(result.value()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.isZero()).isTrue();
    }

    @Test
    @DisplayName("Should multiply money by one")
    void shouldMultiplyMoney_ByOne() {
        Money money = new Money(new BigDecimal("50.00"));

        Money result = money.multiply(1);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Should check if money is greater than another")
    void shouldCheckIfMoney_IsGreaterThanAnother() {
        Money bigger = new Money(new BigDecimal("100.00"));
        Money smaller = new Money(new BigDecimal("50.00"));

        assertThat(bigger.isGreaterThan(smaller)).isTrue();
        assertThat(smaller.isGreaterThan(bigger)).isFalse();
    }

    @Test
    @DisplayName("Should check if equal money values are not greater")
    void shouldCheckIfEqualMoneyValues_AreNotGreater() {
        Money money1 = new Money(new BigDecimal("50.00"));
        Money money2 = new Money(new BigDecimal("50.00"));

        assertThat(money1.isGreaterThan(money2)).isFalse();
        assertThat(money2.isGreaterThan(money1)).isFalse();
    }

    @Test
    @DisplayName("Should check if money is zero")
    void shouldCheckIfMoney_IsZero() {
        Money zero = Money.zero();
        Money nonZero = new Money(new BigDecimal("10.00"));

        assertThat(zero.isZero()).isTrue();
        assertThat(nonZero.isZero()).isFalse();
    }

    @Test
    @DisplayName("Should handle edge cases with very small values")
    void shouldHandleEdgeCases_WithVerySmallValues() {
        Money money = new Money(new BigDecimal("0.01"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("0.01"));
        assertThat(money.isZero()).isFalse();
        assertThat(money.isGreaterThan(Money.zero())).isTrue();
    }

    @Test
    @DisplayName("Should handle complex arithmetic operations")
    void shouldHandleComplexArithmeticOperations() {
        Money price = new Money(new BigDecimal("19.99"));
        Money total = price.multiply(3);
        Money discount = new Money(new BigDecimal("5.00"));
        Money finalAmount = total.add(discount);

        assertThat(total.value()).isEqualByComparingTo(new BigDecimal("59.97"));
        assertThat(finalAmount.value()).isEqualByComparingTo(new BigDecimal("64.97"));
    }
}
