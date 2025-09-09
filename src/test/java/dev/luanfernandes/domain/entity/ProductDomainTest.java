package dev.luanfernandes.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for ProductDomain")
class ProductDomainTest {

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProduct_WithValidData() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        String description = "Test Description";
        Money price = new Money(new BigDecimal("99.99"));
        String category = "ELECTRONICS";
        int stock = 10;
        LocalDateTime createdAt = LocalDateTime.now();

        ProductDomain product = new ProductDomain(id, name, description, price, category, stock, createdAt);

        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getCategory()).isEqualTo(category);
        assertThat(product.getStockQuantity()).isEqualTo(stock);
        assertThat(product.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Should check if product has stock when stock is positive")
    void shouldCheckIfProductHasStock_WhenStockIsPositive() {
        ProductDomain product = createProduct(10);

        assertThat(product.hasStock()).isTrue();
    }

    @Test
    @DisplayName("Should check if product has no stock when stock is zero")
    void shouldCheckIfProductHasNoStock_WhenStockIsZero() {
        ProductDomain product = createProduct(0);

        assertThat(product.hasStock()).isFalse();
    }

    @Test
    @DisplayName("Should check if product has enough stock when quantity is available")
    void shouldCheckIfProductHasEnoughStock_WhenQuantityIsAvailable() {
        ProductDomain product = createProduct(10);

        assertThat(product.hasEnoughStock(5)).isTrue();
        assertThat(product.hasEnoughStock(10)).isTrue();
    }

    @Test
    @DisplayName("Should check if product has insufficient stock when quantity exceeds available")
    void shouldCheckIfProductHasInsufficientStock_WhenQuantityExceedsAvailable() {
        ProductDomain product = createProduct(5);

        assertThat(product.hasEnoughStock(6)).isFalse();
        assertThat(product.hasEnoughStock(10)).isFalse();
    }

    @Test
    @DisplayName("Should reduce stock successfully when sufficient stock available")
    void shouldReduceStock_WhenSufficientStockAvailable() {
        ProductDomain product = createProduct(10);

        ProductDomain reducedProduct = product.reduceStock(3);

        assertThat(reducedProduct.getStockQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("Should reduce entire stock when requesting exact amount")
    void shouldReduceEntireStock_WhenRequestingExactAmount() {
        ProductDomain product = createProduct(5);

        ProductDomain reducedProduct = product.reduceStock(5);

        assertThat(reducedProduct.getStockQuantity()).isZero();
        assertThat(reducedProduct.hasStock()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when trying to reduce more stock than available")
    void shouldThrowException_WhenTryingToReduceMoreStockThanAvailable() {
        ProductDomain product = createProduct(5);

        assertThatThrownBy(() -> product.reduceStock(6))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient stock. Available: 5, Required: 6");
    }

    @Test
    @DisplayName("Should throw exception when trying to reduce from product with no stock")
    void shouldThrowException_WhenTryingToReduceFromProductWithNoStock() {
        ProductDomain product = createProduct(0);

        assertThatThrownBy(() -> product.reduceStock(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient stock. Available: 0, Required: 1");
    }

    @Test
    @DisplayName("Should update timestamp successfully")
    void shouldUpdateTimestamp_Successfully() {
        ProductDomain product = createProduct(5);
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();

        product.updateTimestamp();

        assertThat(product.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should handle edge cases for stock operations")
    void shouldHandleEdgeCases_ForStockOperations() {
        ProductDomain product = createProduct(1);

        ProductDomain reducedProduct = product.reduceStock(1);
        assertThat(reducedProduct.getStockQuantity()).isZero();
        assertThat(reducedProduct.hasStock()).isFalse();

        assertThat(product.hasEnoughStock(1)).isTrue();
        assertThat(reducedProduct.hasEnoughStock(1)).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when product name is null") // FIXME
    void shouldThrowException_WhenProductNameIsNull() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        null,
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "ELECTRONICS",
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when product name is empty")
    void shouldThrowException_WhenProductNameIsEmpty() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "ELECTRONICS",
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when product name is only whitespace")
    void shouldThrowException_WhenProductNameIsOnlyWhitespace() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "   ",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "ELECTRONICS",
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when product category is null")
    void shouldThrowException_WhenProductCategoryIsNull() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "Test Product",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        null,
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product category cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when product category is empty")
    void shouldThrowException_WhenProductCategoryIsEmpty() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "Test Product",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "",
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product category cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when product category is only whitespace")
    void shouldThrowException_WhenProductCategoryIsOnlyWhitespace() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "Test Product",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "   ",
                        10,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product category cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when stock quantity is negative")
    void shouldThrowException_WhenStockQuantityIsNegative() {
        assertThatThrownBy(() -> new ProductDomain(
                        ProductId.generate(),
                        "Test Product",
                        "Test Description",
                        new Money(new BigDecimal("99.99")),
                        "ELECTRONICS",
                        -1,
                        LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock quantity cannot be negative");
    }

    private ProductDomain createProduct(int stock) {
        return new ProductDomain(
                ProductId.generate(),
                "Test Product",
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                stock,
                LocalDateTime.now());
    }
}
