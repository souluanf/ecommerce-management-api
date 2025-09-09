package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.command.CreateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CreateProductUseCase")
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct_WhenValidCommand() {
        CreateProductCommand command = new CreateProductCommand(
                "Test Product", "Test Description", new Money(new BigDecimal("99.99")), "ELECTRONICS", 100);

        ProductDomain savedProduct = createProduct(command);

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        ProductDomain result = createProductUseCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(command.name());
        assertThat(result.getDescription()).isEqualTo(command.description());
        assertThat(result.getPrice()).isEqualTo(command.price());
        assertThat(result.getCategory()).isEqualTo(command.category());
        assertThat(result.getStockQuantity()).isEqualTo(command.stockQuantity());

        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(savedProduct);
    }

    @Test
    @DisplayName("Should create product even when indexing fails")
    void shouldCreateProduct_WhenIndexingFails() {
        CreateProductCommand command = new CreateProductCommand(
                "Test Product", "Test Description", new Money(new BigDecimal("99.99")), "ELECTRONICS", 100);

        ProductDomain savedProduct = createProduct(command);

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);
        doThrow(new RuntimeException("Indexing failed"))
                .when(productSearchRepository)
                .index(any(ProductDomain.class));

        ProductDomain result = createProductUseCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(command.name());

        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(savedProduct);
    }

    @Test
    @DisplayName("Should create product with different category")
    void shouldCreateProduct_WithDifferentCategory() {
        CreateProductCommand command = new CreateProductCommand(
                "Book Product", "Book Description", new Money(new BigDecimal("29.99")), "BOOKS", 50);

        ProductDomain savedProduct = createProduct(command);

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        ProductDomain result = createProductUseCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("BOOKS");

        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(savedProduct);
    }

    @Test
    @DisplayName("Should create product with zero stock")
    void shouldCreateProduct_WithZeroStock() {
        CreateProductCommand command = new CreateProductCommand(
                "Out of Stock Product", "No stock available", new Money(new BigDecimal("199.99")), "CLOTHING", 0);

        ProductDomain savedProduct = createProduct(command);

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        ProductDomain result = createProductUseCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getStockQuantity()).isZero();

        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(savedProduct);
    }

    private ProductDomain createProduct(CreateProductCommand command) {
        return new ProductDomain(
                ProductId.generate(),
                command.name(),
                command.description(),
                command.price(),
                command.category(),
                command.stockQuantity(),
                LocalDateTime.now());
    }
}
