package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductInUseException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
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
@DisplayName("Tests for DeleteProductUseCase")
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProduct_WhenProductExistsAndNotInUse() {
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(orderRepository.existsByProductId(productId)).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        deleteProductUseCase.delete(productId);

        verify(productRepository).existsById(productId);
        verify(orderRepository).existsByProductId(productId);
        verify(productRepository).findById(productId);
        verify(productRepository).delete(productId);
        verify(productSearchRepository).delete(product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundException_WhenProductNotFound() {
        ProductId productId = ProductId.generate();

        when(productRepository.existsById(productId)).thenReturn(false);

        assertThatThrownBy(() -> deleteProductUseCase.delete(productId)).isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).existsById(productId);
    }

    @Test
    @DisplayName("Should throw ProductInUseException when product is associated with orders")
    void shouldThrowProductInUseException_WhenProductIsInUse() {
        ProductId productId = ProductId.generate();

        when(productRepository.existsById(productId)).thenReturn(true);
        when(orderRepository.existsByProductId(productId)).thenReturn(true);

        assertThatThrownBy(() -> deleteProductUseCase.delete(productId)).isInstanceOf(ProductInUseException.class);

        verify(productRepository).existsById(productId);
        verify(orderRepository).existsByProductId(productId);
    }

    @Test
    @DisplayName("Should delete product even when search index removal fails")
    void shouldDeleteProduct_WhenSearchIndexRemovalFails() {
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(orderRepository.existsByProductId(productId)).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doThrow(new RuntimeException("Search index error"))
                .when(productSearchRepository)
                .delete(any(ProductDomain.class));

        deleteProductUseCase.delete(productId);

        verify(productRepository).existsById(productId);
        verify(orderRepository).existsByProductId(productId);
        verify(productRepository).findById(productId);
        verify(productRepository).delete(productId);
        verify(productSearchRepository).delete(product);
    }

    @Test
    @DisplayName("Should handle deletion when product not found during findById")
    void shouldHandleDeletion_WhenProductNotFoundDuringFindById() {
        ProductId productId = ProductId.generate();

        when(productRepository.existsById(productId)).thenReturn(true);
        when(orderRepository.existsByProductId(productId)).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        deleteProductUseCase.delete(productId);

        verify(productRepository).existsById(productId);
        verify(orderRepository).existsByProductId(productId);
        verify(productRepository).findById(productId);
        verify(productRepository).delete(productId);
    }

    private ProductDomain createProduct(ProductId productId) {
        return new ProductDomain(
                productId,
                "Test Product",
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                100,
                LocalDateTime.now());
    }
}
