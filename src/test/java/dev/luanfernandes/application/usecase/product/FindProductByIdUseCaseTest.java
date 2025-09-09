package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
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
@DisplayName("Tests for FindProductByIdUseCase")
class FindProductByIdUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FindProductByIdUseCase findProductByIdUseCase;

    @Test
    @DisplayName("Should return product when product exists")
    void shouldReturnProduct_WhenProductExists() {
        ProductId productId = ProductId.generate();
        ProductDomain expectedProduct = createProduct(productId, "Test Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        ProductDomain result = findProductByIdUseCase.execute(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPrice()).isEqualTo(new Money(new BigDecimal("99.99")));
        assertThat(result.getCategory()).isEqualTo("ELECTRONICS");
        assertThat(result.getStockQuantity()).isEqualTo(100);

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundException_WhenProductNotFound() {
        ProductId productId = ProductId.generate();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findProductByIdUseCase.execute(productId))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should return product with different category")
    void shouldReturnProduct_WithDifferentCategory() {
        ProductId productId = ProductId.generate();
        ProductDomain bookProduct = createBookProduct(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(bookProduct));

        ProductDomain result = findProductByIdUseCase.execute(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Book Product");
        assertThat(result.getCategory()).isEqualTo("BOOKS");
        assertThat(result.getPrice()).isEqualTo(new Money(new BigDecimal("29.99")));

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should return product with zero stock")
    void shouldReturnProduct_WithZeroStock() {
        ProductId productId = ProductId.generate();
        ProductDomain outOfStockProduct = createOutOfStockProduct(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(outOfStockProduct));

        ProductDomain result = findProductByIdUseCase.execute(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getStockQuantity()).isZero();

        verify(productRepository).findById(productId);
    }

    private ProductDomain createProduct(ProductId productId, String name) {
        return new ProductDomain(
                productId,
                name,
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                100,
                LocalDateTime.now());
    }

    private ProductDomain createBookProduct(ProductId productId) {
        return new ProductDomain(
                productId,
                "Book Product",
                "Book Description",
                new Money(new BigDecimal("29.99")),
                "BOOKS",
                50,
                LocalDateTime.now());
    }

    private ProductDomain createOutOfStockProduct(ProductId productId) {
        return new ProductDomain(
                productId,
                "Out of Stock Product",
                "No stock available",
                new Money(new BigDecimal("199.99")),
                "CLOTHING",
                0,
                LocalDateTime.now());
    }
}
