package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.command.UpdateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UpdateProductUseCase")
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProduct_WhenProductExists() {
        ProductId productId = ProductId.generate();
        ProductDomain existingProduct = createExistingProduct(productId);
        UpdateProductCommand command = createUpdateCommand(productId);
        ProductDomain updatedProduct = createUpdatedProduct(productId, command);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(updatedProduct);

        Optional<ProductDomain> result = updateProductUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Product");
        assertThat(result.get().getDescription()).isEqualTo("Updated Description");
        assertThat(result.get().getPrice()).isEqualTo(new Money(new BigDecimal("149.99")));
        assertThat(result.get().getCategory()).isEqualTo("BOOKS");
        assertThat(result.get().getStockQuantity()).isEqualTo(75);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(updatedProduct);
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void shouldReturnEmpty_WhenProductNotFound() {
        ProductId productId = ProductId.generate();
        UpdateProductCommand command = createUpdateCommand(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<ProductDomain> result = updateProductUseCase.update(command);

        assertThat(result).isEmpty();

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should update product even when reindexing fails")
    void shouldUpdateProduct_WhenReindexingFails() {
        ProductId productId = ProductId.generate();
        ProductDomain existingProduct = createExistingProduct(productId);
        UpdateProductCommand command = createUpdateCommand(productId);
        ProductDomain updatedProduct = createUpdatedProduct(productId, command);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(updatedProduct);
        doThrow(new RuntimeException("Reindexing failed"))
                .when(productSearchRepository)
                .index(any(ProductDomain.class));

        Optional<ProductDomain> result = updateProductUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Product");

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
        verify(productSearchRepository).index(updatedProduct);
    }

    @Test
    @DisplayName("Should preserve created date when updating")
    void shouldPreserveCreatedDate_WhenUpdating() {
        ProductId productId = ProductId.generate();
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        ProductDomain existingProduct = createExistingProductWithDate(productId, originalCreatedAt);
        UpdateProductCommand command = createUpdateCommand(productId);
        ProductDomain updatedProduct = createUpdatedProductWithDate(productId, command, originalCreatedAt);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(updatedProduct);

        Optional<ProductDomain> result = updateProductUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getCreatedAt()).isEqualTo(originalCreatedAt);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should update all product fields")
    void shouldUpdateAllFields_WhenCommandProvided() {
        ProductId productId = ProductId.generate();
        ProductDomain existingProduct = createExistingProduct(productId);
        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                "Completely New Name",
                "Completely New Description",
                new Money(new BigDecimal("999.99")),
                "CLOTHING",
                200);
        ProductDomain updatedProduct = createUpdatedProduct(productId, command);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(updatedProduct);

        Optional<ProductDomain> result = updateProductUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Completely New Name");
        assertThat(result.get().getDescription()).isEqualTo("Completely New Description");
        assertThat(result.get().getPrice()).isEqualTo(new Money(new BigDecimal("999.99")));
        assertThat(result.get().getCategory()).isEqualTo("CLOTHING");
        assertThat(result.get().getStockQuantity()).isEqualTo(200);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
    }

    private ProductDomain createExistingProduct(ProductId productId) {
        return new ProductDomain(
                productId,
                "Original Product",
                "Original Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                100,
                LocalDateTime.now());
    }

    private ProductDomain createExistingProductWithDate(ProductId productId, LocalDateTime createdAt) {
        return new ProductDomain(
                productId,
                "Original Product",
                "Original Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                100,
                createdAt);
    }

    private UpdateProductCommand createUpdateCommand(ProductId productId) {
        return new UpdateProductCommand(
                productId, "Updated Product", "Updated Description", new Money(new BigDecimal("149.99")), "BOOKS", 75);
    }

    private ProductDomain createUpdatedProduct(ProductId productId, UpdateProductCommand command) {
        return new ProductDomain(
                productId,
                command.name(),
                command.description(),
                command.price(),
                command.category(),
                command.stockQuantity(),
                LocalDateTime.now());
    }

    private ProductDomain createUpdatedProductWithDate(
            ProductId productId, UpdateProductCommand command, LocalDateTime createdAt) {
        return new ProductDomain(
                productId,
                command.name(),
                command.description(),
                command.price(),
                command.category(),
                command.stockQuantity(),
                createdAt);
    }
}
