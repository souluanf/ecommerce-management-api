package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ListAllProductsUseCase")
class ListAllProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ListAllProductsUseCase listAllProductsUseCase;

    @Test
    @DisplayName("Should return all products when products exist")
    void shouldReturnAllProducts_WhenProductsExist() {
        List<ProductDomain> expectedProducts = Arrays.asList(
                createProduct("Product 1", "ELECTRONICS"),
                createProduct("Product 2", "BOOKS"),
                createProduct("Product 3", "CLOTHING"));

        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<ProductDomain> result = listAllProductsUseCase.execute();

        assertThat(result).isNotNull().hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Product 1");
        assertThat(result.get(1).getName()).isEqualTo("Product 2");
        assertThat(result.get(2).getName()).isEqualTo("Product 3");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyList_WhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductDomain> result = listAllProductsUseCase.execute();

        assertThat(result).isNotNull().isEmpty();

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return paginated products when page request provided")
    void shouldReturnPaginatedProducts_WhenPageRequestProvided() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ProductDomain> products =
                Arrays.asList(createProduct("Product 1", "ELECTRONICS"), createProduct("Product 2", "BOOKS"));
        PageResponse<ProductDomain> expectedPageResponse = new PageResponse<>(0, 10, 2L, false, products);

        when(productRepository.findAllPaginated(pageRequest)).thenReturn(expectedPageResponse);

        PageResponse<ProductDomain> result = listAllProductsUseCase.execute(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.elements()).isEqualTo(2L);

        verify(productRepository).findAllPaginated(pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no products exist with pagination")
    void shouldReturnEmptyPage_WhenNoProductsExistWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageResponse<ProductDomain> expectedPageResponse =
                new PageResponse<>(0, 10, 0L, false, Collections.emptyList());

        when(productRepository.findAllPaginated(pageRequest)).thenReturn(expectedPageResponse);

        PageResponse<ProductDomain> result = listAllProductsUseCase.execute(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.elements()).isZero();

        verify(productRepository).findAllPaginated(pageRequest);
    }

    @Test
    @DisplayName("Should handle different page sizes")
    void shouldHandleDifferentPageSizes() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<ProductDomain> products = Arrays.asList(createProduct("Product 6", "ELECTRONICS"));
        PageResponse<ProductDomain> expectedPageResponse = new PageResponse<>(1, 5, 11L, true, products);

        when(productRepository.findAllPaginated(pageRequest)).thenReturn(expectedPageResponse);

        PageResponse<ProductDomain> result = listAllProductsUseCase.execute(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.elements()).isEqualTo(11L);

        verify(productRepository).findAllPaginated(pageRequest);
    }

    private ProductDomain createProduct(String name, String category) {
        return new ProductDomain(
                ProductId.generate(),
                name,
                "Description for " + name,
                new Money(new BigDecimal("99.99")),
                category,
                100,
                LocalDateTime.now());
    }
}
