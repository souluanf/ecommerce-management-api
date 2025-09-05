package dev.luanfernandes.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal value) {

    public Money {
        if (value == null) {
            throw new IllegalArgumentException("Money value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money value cannot be negative: " + value);
        }
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money multiply(int quantity) {
        return new Money(this.value.multiply(BigDecimal.valueOf(quantity)));
    }

    public boolean isGreaterThan(Money other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }
}
