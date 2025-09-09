package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.result.ReindexResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ReindexFailedException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ReindexProductsUseCase")
class ReindexProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private ReindexProductsUseCase reindexProductsUseCase;

    @Test
    @DisplayName("Should reindex all products successfully")
    void shouldReindexAllProducts_Successfully() {
        List<ProductDomain> products =
                Arrays.asList(createProduct("Product 1"), createProduct("Product 2"), createProduct("Product 3"));

        when(productRepository.findAll()).thenReturn(products);

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(3);
        assertThat(result.indexedProducts()).isEqualTo(3);
        assertThat(result.message()).isEqualTo("Reindex completed successfully");

        verify(productRepository).findAll();
        verify(productSearchRepository, times(3)).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should return appropriate result when no products exist")
    void shouldReturnAppropriateResult_WhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isZero();
        assertThat(result.indexedProducts()).isZero();
        assertThat(result.message()).isEqualTo("No products found");

        verify(productRepository).findAll();
        verify(productSearchRepository, never()).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should handle partial failures during indexing")
    void shouldHandlePartialFailures_DuringIndexing() {
        ProductDomain product1 = createProduct("Product 1");
        ProductDomain product2 = createProduct("Product 2");
        ProductDomain product3 = createProduct("Product 3");
        List<ProductDomain> products = Arrays.asList(product1, product2, product3);

        when(productRepository.findAll()).thenReturn(products);
        doThrow(new RuntimeException("Indexing failed"))
                .when(productSearchRepository)
                .index(any(ProductDomain.class));

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(3);
        assertThat(result.indexedProducts()).isZero();
        assertThat(result.message()).isEqualTo("Reindex completed with 3 failures");

        verify(productRepository).findAll();
        verify(productSearchRepository, times(3)).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should handle all products failing to index")
    void shouldHandleAllProductsFailingToIndex() {
        List<ProductDomain> products = Arrays.asList(createProduct("Product 1"), createProduct("Product 2"));

        when(productRepository.findAll()).thenReturn(products);
        doThrow(new RuntimeException("Indexing failed"))
                .when(productSearchRepository)
                .index(any(ProductDomain.class));

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(2);
        assertThat(result.indexedProducts()).isZero();
        assertThat(result.message()).isEqualTo("Reindex completed with 2 failures");

        verify(productRepository).findAll();
        verify(productSearchRepository, times(2)).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should throw ReindexFailedException when repository throws exception")
    void shouldThrowReindexFailedException_WhenRepositoryThrowsException() {
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> reindexProductsUseCase.execute())
                .isInstanceOf(ReindexFailedException.class)
                .hasMessage("Reindex failed: Database error")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(productRepository).findAll();
        verify(productSearchRepository, never()).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should reindex single product successfully")
    void shouldReindexSingleProduct_Successfully() {
        ProductDomain singleProduct = createProduct("Single Product");
        List<ProductDomain> products = Collections.singletonList(singleProduct);

        when(productRepository.findAll()).thenReturn(products);

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(1);
        assertThat(result.indexedProducts()).isEqualTo(1);
        assertThat(result.message()).isEqualTo("Reindex completed successfully");

        verify(productRepository).findAll();
        verify(productSearchRepository).index(singleProduct);
    }

    @Test
    @DisplayName("Should handle large number of products")
    void shouldHandleLargeNumberOfProducts() {
        List<ProductDomain> products = Arrays.asList(
                createProduct("Product 1"),
                createProduct("Product 2"),
                createProduct("Product 3"),
                createProduct("Product 4"),
                createProduct("Product 5"),
                createProduct("Product 6"),
                createProduct("Product 7"),
                createProduct("Product 8"),
                createProduct("Product 9"),
                createProduct("Product 10"));

        when(productRepository.findAll()).thenReturn(products);

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(10);
        assertThat(result.indexedProducts()).isEqualTo(10);
        assertThat(result.message()).isEqualTo("Reindex completed successfully");

        verify(productRepository).findAll();
        verify(productSearchRepository, times(10)).index(any(ProductDomain.class));
    }

    @Test
    @DisplayName("Should handle mixed success and failure scenarios")
    void shouldHandleMixedSuccessAndFailureScenarios() {
        List<ProductDomain> products = Arrays.asList(
                createProduct("Product 1"),
                createProduct("Product 2"),
                createProduct("Product 3"),
                createProduct("Product 4"),
                createProduct("Product 5"));

        when(productRepository.findAll()).thenReturn(products);

        AtomicInteger callCount = new AtomicInteger(0);
        doAnswer(invocation -> {
                    int count = callCount.incrementAndGet();
                    if (count == 2 || count == 4) {
                        throw new RuntimeException("Indexing failed");
                    }
                    return null;
                })
                .when(productSearchRepository)
                .index(any(ProductDomain.class));

        ReindexResult result = reindexProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalProducts()).isEqualTo(5);
        assertThat(result.indexedProducts()).isEqualTo(3);
        assertThat(result.message()).isEqualTo("Reindex completed with 2 failures");

        verify(productRepository).findAll();
        verify(productSearchRepository, times(5)).index(any(ProductDomain.class));
    }

    private ProductDomain createProduct(String name) {
        return new ProductDomain(
                ProductId.generate(),
                name,
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                10,
                LocalDateTime.now());
    }
}
