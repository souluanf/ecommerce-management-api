package dev.luanfernandes.adapter.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luanfernandes.adapter.out.persistence.entity.ProductJpaEntity;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductEntityMapperTest {

    private ProductEntityMapper productEntityMapper;

    @BeforeEach
    void setUp() {
        productEntityMapper = new ProductEntityMapper();
    }

    @Test
    void shouldMapProductJpaEntityToDomain() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductJpaEntity productEntity = new ProductJpaEntity(
                productId.toString(),
                "Test Product",
                "Test Description",
                new BigDecimal("29.99"),
                "Electronics",
                10,
                createdAt);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);

        assertThat(productDomain).isNotNull();
        assertThat(productDomain.getId().value()).isEqualTo(productId.toString());
        assertThat(productDomain.getName()).isEqualTo("Test Product");
        assertThat(productDomain.getDescription()).isEqualTo("Test Description");
        assertThat(productDomain.getPrice().value()).isEqualTo(new BigDecimal("29.99"));
        assertThat(productDomain.getCategory()).isEqualTo("Electronics");
        assertThat(productDomain.getStockQuantity()).isEqualTo(10);
        assertThat(productDomain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldMapProductDomainToEntity() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductDomain productDomain = new ProductDomain(
                ProductId.of(productId),
                "Test Product",
                "Test Description",
                Money.of(new BigDecimal("29.99")),
                "Electronics",
                10,
                createdAt);

        ProductJpaEntity productEntity = productEntityMapper.toEntity(productDomain);

        assertThat(productEntity).isNotNull();
        assertThat(productEntity.getId()).isEqualTo(productId.toString());
        assertThat(productEntity.getName()).isEqualTo("Test Product");
        assertThat(productEntity.getDescription()).isEqualTo("Test Description");
        assertThat(productEntity.getPrice()).isEqualTo(new BigDecimal("29.99"));
        assertThat(productEntity.getCategory()).isEqualTo("Electronics");
        assertThat(productEntity.getStockQuantity()).isEqualTo(10);
        assertThat(productEntity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldHandleZeroPrice() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductJpaEntity productEntity = new ProductJpaEntity(
                productId.toString(), "Free Product", "Free Description", BigDecimal.ZERO, "Free", 100, createdAt);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);

        assertThat(productDomain.getPrice().value()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(productDomain.getName()).isEqualTo("Free Product");
        assertThat(productDomain.getCategory()).isEqualTo("Free");
    }

    @Test
    void shouldHandleZeroStock() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductDomain productDomain = new ProductDomain(
                ProductId.of(productId),
                "Out of Stock Product",
                "Out of stock description",
                Money.of(new BigDecimal("99.99")),
                "Limited",
                0,
                createdAt);

        ProductJpaEntity productEntity = productEntityMapper.toEntity(productDomain);

        assertThat(productEntity.getStockQuantity()).isZero();
        assertThat(productEntity.getName()).isEqualTo("Out of Stock Product");
        assertThat(productEntity.getPrice()).isEqualTo(new BigDecimal("99.99"));
    }

    @Test
    void shouldHandleMinimalProductName() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductJpaEntity productEntity =
                new ProductJpaEntity(productId.toString(), "P", "", new BigDecimal("10.00"), "General", 5, createdAt);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);

        assertThat(productDomain.getName()).isEqualTo("P");
        assertThat(productDomain.getDescription()).isEmpty();
        assertThat(productDomain.getCategory()).isEqualTo("General");
        assertThat(productDomain.getPrice().value()).isEqualTo(new BigDecimal("10.00"));
        assertThat(productDomain.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void shouldHandleNullDescription() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductJpaEntity productEntity = new ProductJpaEntity(
                productId.toString(), "Product Name", null, new BigDecimal("15.50"), "Category", 3, createdAt);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);

        assertThat(productDomain.getName()).isEqualTo("Product Name");
        assertThat(productDomain.getDescription()).isNull();
        assertThat(productDomain.getCategory()).isEqualTo("Category");
    }

    @Test
    void shouldHandleHighStockQuantity() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductDomain productDomain = new ProductDomain(
                ProductId.of(productId),
                "Bulk Product",
                "Bulk Description",
                Money.of(new BigDecimal("1.99")),
                "Bulk",
                1000000,
                createdAt);

        ProductJpaEntity productEntity = productEntityMapper.toEntity(productDomain);

        assertThat(productEntity.getStockQuantity()).isEqualTo(1000000);
        assertThat(productEntity.getName()).isEqualTo("Bulk Product");
    }

    @Test
    void shouldHandleHighPrice() {

        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ProductJpaEntity productEntity = new ProductJpaEntity(
                productId.toString(),
                "Expensive Product",
                "Very expensive",
                new BigDecimal("999999.99"),
                "Luxury",
                1,
                createdAt);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);

        assertThat(productDomain.getPrice().value()).isEqualTo(new BigDecimal("999999.99"));
        assertThat(productDomain.getName()).isEqualTo("Expensive Product");
        assertThat(productDomain.getCategory()).isEqualTo("Luxury");
    }

    @Test
    void shouldPreserveTimestamps() {

        UUID productId = UUID.randomUUID();
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

        ProductJpaEntity productEntity = new ProductJpaEntity(
                productId.toString(),
                "Time Test Product",
                "Time test",
                new BigDecimal("12.34"),
                "Test",
                7,
                specificTime);

        ProductDomain productDomain = productEntityMapper.toDomain(productEntity);
        ProductJpaEntity mappedBackEntity = productEntityMapper.toEntity(productDomain);

        assertThat(productDomain.getCreatedAt()).isEqualTo(specificTime);
        assertThat(mappedBackEntity.getCreatedAt()).isEqualTo(specificTime);
    }
}
