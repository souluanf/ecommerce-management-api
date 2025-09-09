package dev.luanfernandes.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderItemDomainTest {

    @Test
    void shouldCreateOrderItemWithValidData() {
        ProductId productId = new ProductId("product-123");
        String productName = "Smartphone";
        Money unitPrice = new Money(new BigDecimal("999.99"));
        int quantity = 2;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getProductId()).isEqualTo(productId);
        assertThat(orderItem.getProductName()).isEqualTo(productName);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getSubtotal()).isEqualTo(new Money(new BigDecimal("1999.98")));
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {

        ProductId productId = new ProductId("product-456");
        String productName = "Laptop";
        Money unitPrice = new Money(new BigDecimal("1500.00"));
        int quantity = 3;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getSubtotal()).isEqualTo(new Money(new BigDecimal("4500.00")));
    }

    @Test
    void shouldHandleSingleQuantity() {

        ProductId productId = new ProductId("product-789");
        String productName = "Headphones";
        Money unitPrice = new Money(new BigDecimal("299.99"));
        int quantity = 1;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getQuantity()).isEqualTo(1);
        assertThat(orderItem.getSubtotal()).isEqualTo(unitPrice);
    }

    @Test
    void shouldThrowExceptionForZeroQuantity() {

        ProductId productId = new ProductId("product-999");
        String productName = "Product";
        Money unitPrice = new Money(new BigDecimal("10.00"));
        int quantity = 0;

        assertThatThrownBy(() -> new OrderItemDomain(productId, productName, unitPrice, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldThrowExceptionForNegativeQuantity() {

        ProductId productId = new ProductId("product-negative");
        String productName = "Product";
        Money unitPrice = new Money(new BigDecimal("10.00"));
        int quantity = -1;

        assertThatThrownBy(() -> new OrderItemDomain(productId, productName, unitPrice, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldHandleNullProductId() {

        ProductId nullProductId = null;
        String productName = "Product";
        Money unitPrice = new Money(new BigDecimal("10.00"));
        int quantity = 1;

        OrderItemDomain orderItem = new OrderItemDomain(nullProductId, productName, unitPrice, quantity);

        assertThat(orderItem.getProductId()).isNull();
        assertThat(orderItem.getProductName()).isEqualTo(productName);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
    }

    @Test
    void shouldHandleNullProductName() {

        ProductId productId = new ProductId("product-null-name");
        String nullProductName = null;
        Money unitPrice = new Money(new BigDecimal("50.00"));
        int quantity = 2;

        OrderItemDomain orderItem = new OrderItemDomain(productId, nullProductName, unitPrice, quantity);

        assertThat(orderItem.getProductId()).isEqualTo(productId);
        assertThat(orderItem.getProductName()).isNull();
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getSubtotal()).isEqualTo(new Money(new BigDecimal("100.00")));
    }

    @Test
    void shouldHandleEmptyProductName() {

        ProductId productId = new ProductId("product-empty-name");
        String emptyProductName = "";
        Money unitPrice = new Money(new BigDecimal("25.00"));
        int quantity = 1;

        OrderItemDomain orderItem = new OrderItemDomain(productId, emptyProductName, unitPrice, quantity);

        assertThat(orderItem.getProductName()).isEmpty();
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getSubtotal()).isEqualTo(unitPrice);
    }

    @Test
    void shouldThrowExceptionForNullUnitPrice() {

        ProductId productId = new ProductId("product-null-price");
        String productName = "Free Product";
        Money nullUnitPrice = null;
        int quantity = 1;

        assertThatThrownBy(() -> new OrderItemDomain(productId, productName, nullUnitPrice, quantity))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(
                        "Cannot invoke \"dev.luanfernandes.domain.valueobject.Money.multiply(int)\" because \"unitPrice\" is null");
    }

    @Test
    void shouldCalculateSubtotalWithLargeQuantity() {

        ProductId productId = new ProductId("bulk-product");
        String productName = "Bulk Item";
        Money unitPrice = new Money(new BigDecimal("1.99"));
        int quantity = 1000;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getQuantity()).isEqualTo(1000);
        assertThat(orderItem.getSubtotal()).isEqualTo(new Money(new BigDecimal("1990.00")));
    }

    @Test
    void shouldHandleDecimalPriceWithOddQuantity() {

        ProductId productId = new ProductId("decimal-product");
        String productName = "Decimal Price Product";
        Money unitPrice = new Money(new BigDecimal("33.33"));
        int quantity = 3;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getSubtotal()).isEqualTo(new Money(new BigDecimal("99.99")));
    }

    @Test
    void shouldPreserveImmutabilityOfFields() {

        ProductId productId = new ProductId("immutable-test");
        String productName = "Immutable Product";
        Money unitPrice = new Money(new BigDecimal("100.00"));
        int quantity = 5;

        OrderItemDomain orderItem = new OrderItemDomain(productId, productName, unitPrice, quantity);

        assertThat(orderItem.getProductId()).isSameAs(productId);
        assertThat(orderItem.getProductName()).isSameAs(productName);
        assertThat(orderItem.getUnitPrice()).isSameAs(unitPrice);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getSubtotal()).isNotNull();
    }
}
