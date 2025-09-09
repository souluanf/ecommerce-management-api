package dev.luanfernandes.application.usecase.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.search.domain.SearchCriteria;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
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
@DisplayName("Tests for SearchProductsUseCase")
class SearchProductsUseCaseTest {

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private SearchProductsUseCase searchProductsUseCase;

    @Test
    @DisplayName("Should search products with basic query")
    void shouldSearchProducts_WithBasicQuery() {
        SearchCriteria criteria = new SearchCriteria("laptop", null, null, null, 0, 10, null, null);

        List<ProductDomain> products = Arrays.asList(
                createProduct("Gaming Laptop", "ELECTRONICS"), createProduct("Business Laptop", "ELECTRONICS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 2L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.currentPage()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search products with category filter")
    void shouldSearchProducts_WithCategoryFilter() {
        SearchCriteria criteria = new SearchCriteria(null, "ELECTRONICS", null, null, 0, 5, null, null);

        List<ProductDomain> products = Collections.singletonList(createProduct("Smartphone", "ELECTRONICS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 0, 5, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);
        assertThat(result.products().get(0).getCategory()).isEqualTo("ELECTRONICS");

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search products with price range")
    void shouldSearchProducts_WithPriceRange() {
        SearchCriteria criteria =
                new SearchCriteria(null, null, new BigDecimal("100.00"), new BigDecimal("500.00"), 0, 10, null, null);

        List<ProductDomain> products = Arrays.asList(
                createProductWithPrice("Product 1", new BigDecimal("150.00")),
                createProductWithPrice("Product 2", new BigDecimal("300.00")));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 2L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search products with all filters combined")
    void shouldSearchProducts_WithAllFiltersCombined() {
        SearchCriteria criteria = new SearchCriteria(
                "gaming", "ELECTRONICS", new BigDecimal("200.00"), new BigDecimal("1000.00"), 1, 5, null, null);

        List<ProductDomain> products =
                Collections.singletonList(createProductWithPrice("Gaming Mouse", new BigDecimal("250.00")));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 1, 5, false, true);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);
        assertThat(result.currentPage()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(5);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should return empty result when no products found")
    void shouldReturnEmptyResult_WhenNoProductsFound() {
        SearchCriteria criteria = new SearchCriteria("nonexistent", null, null, null, 0, 10, null, null);

        ProductSearchResult expectedResult =
                new ProductSearchResult(Collections.emptyList(), 0L, 0, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePagination_Correctly() {
        SearchCriteria criteria = new SearchCriteria("product", null, null, null, 2, 3, null, null);

        List<ProductDomain> products = Arrays.asList(
                createProduct("Product 7", "ELECTRONICS"),
                createProduct("Product 8", "ELECTRONICS"),
                createProduct("Product 9", "ELECTRONICS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 15L, 5, 2, 3, true, true);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(3);
        assertThat(result.totalElements()).isEqualTo(15);
        assertThat(result.currentPage()).isEqualTo(2);
        assertThat(result.pageSize()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(5);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search with null query")
    void shouldSearch_WithNullQuery() {
        SearchCriteria criteria = new SearchCriteria(null, null, null, null, 0, 10, null, null);

        List<ProductDomain> products =
                Arrays.asList(createProduct("All Product 1", "ELECTRONICS"), createProduct("All Product 2", "BOOKS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 2L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(2);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search with empty query")
    void shouldSearch_WithEmptyQuery() {
        SearchCriteria criteria = new SearchCriteria("", null, null, null, 0, 10, null, null);

        List<ProductDomain> products = Collections.singletonList(createProduct("Random Product", "ELECTRONICS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should handle large page numbers")
    void shouldHandle_LargePageNumbers() {
        SearchCriteria criteria = new SearchCriteria("test", null, null, null, 999, 10, null, null);

        ProductSearchResult expectedResult =
                new ProductSearchResult(Collections.emptyList(), 50L, 5, 999, 10, false, true);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).isEmpty();
        assertThat(result.currentPage()).isEqualTo(999);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should handle small page sizes")
    void shouldHandle_SmallPageSizes() {
        SearchCriteria criteria = new SearchCriteria("product", null, null, null, 0, 1, null, null);

        List<ProductDomain> products = Collections.singletonList(createProduct("Single Product", "ELECTRONICS"));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 10L, 10, 0, 1, true, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);
        assertThat(result.pageSize()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(10);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should handle decimal price ranges")
    void shouldHandle_DecimalPriceRanges() {
        SearchCriteria criteria =
                new SearchCriteria(null, null, new BigDecimal("99.99"), new BigDecimal("199.99"), 0, 10, null, null);

        List<ProductDomain> products =
                Collections.singletonList(createProductWithPrice("Mid-range Product", new BigDecimal("149.50")));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search with only minimum price")
    void shouldSearch_WithOnlyMinimumPrice() {
        SearchCriteria criteria = new SearchCriteria(null, null, new BigDecimal("500.00"), null, 0, 10, null, null);

        List<ProductDomain> products =
                Collections.singletonList(createProductWithPrice("Expensive Product", new BigDecimal("999.99")));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    @Test
    @DisplayName("Should search with only maximum price")
    void shouldSearch_WithOnlyMaximumPrice() {
        SearchCriteria criteria = new SearchCriteria(null, null, null, new BigDecimal("100.00"), 0, 10, null, null);

        List<ProductDomain> products =
                Collections.singletonList(createProductWithPrice("Cheap Product", new BigDecimal("50.00")));
        ProductSearchResult expectedResult = new ProductSearchResult(products, 1L, 1, 0, 10, false, false);

        when(productSearchRepository.search(any(ProductSearchRepository.SearchCriteria.class)))
                .thenReturn(expectedResult);

        ProductSearchResult result = searchProductsUseCase.execute(criteria);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);

        verify(productSearchRepository).search(any(ProductSearchRepository.SearchCriteria.class));
    }

    private ProductDomain createProduct(String name, String category) {
        return new ProductDomain(
                ProductId.generate(),
                name,
                "Test Description",
                new Money(new BigDecimal("99.99")),
                category,
                10,
                LocalDateTime.now());
    }

    private ProductDomain createProductWithPrice(String name, BigDecimal price) {
        return new ProductDomain(
                ProductId.generate(),
                name,
                "Test Description",
                new Money(price),
                "ELECTRONICS",
                10,
                LocalDateTime.now());
    }
}
