package dev.luanfernandes.adapter.out.search.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.SearchException;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository.SearchCriteria;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProductSearchRepositoryImplTest {

    @Mock
    private ProductElasticsearchRepository elasticsearchRepository;

    @Mock
    private Page<ProductDocument> mockPage;

    private ProductSearchRepositoryImpl productSearchRepository;

    @BeforeEach
    void setUp() {
        productSearchRepository = new ProductSearchRepositoryImpl(elasticsearchRepository);
    }

    @Test
    void shouldIndexProductSuccessfully() {

        ProductDomain product = createProductDomain(new BigDecimal("29.99"));

        ProductDocument expectedDocument = ProductDocument.from(
                product.getId().value(),
                product.getName(),
                product.getDescription(),
                product.getPrice().value(),
                product.getCategory(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt());

        when(elasticsearchRepository.save(any(ProductDocument.class))).thenReturn(expectedDocument);

        productSearchRepository.index(product);

        verify(elasticsearchRepository).save(any(ProductDocument.class));
    }

    @Test
    void shouldDeleteProductFromIndex() {

        ProductDomain product = createProductDomain(new BigDecimal("29.99"));

        productSearchRepository.delete(product);

        verify(elasticsearchRepository).deleteById("product-1");
    }

    @Test
    void shouldSearchByQueryOnly() {

        SearchCriteria criteria = new SearchCriteria("laptop", null, null, null, 0, 10);
        List<ProductDocument> documents = List.of(
                createProductDocument("1", "Gaming Laptop", "Electronics"),
                createProductDocument("2", "Business Laptop", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 2);
        when(elasticsearchRepository.findByQueryAndAvailableTrue(eq("laptop"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.products().getFirst().getName()).isEqualTo("Gaming Laptop");
        verify(elasticsearchRepository).findByQueryAndAvailableTrue(eq("laptop"), any(PageRequest.class));
    }

    @Test
    void shouldSearchByQueryAndCategory() {

        SearchCriteria criteria = new SearchCriteria("phone", "Electronics", null, null, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "iPhone", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndCategoryAndAvailableTrue(
                        eq("phone"), eq("Electronics"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        assertThat(result.products().getFirst().getName()).isEqualTo("iPhone");
        assertThat(result.products().getFirst().getCategory()).isEqualTo("Electronics");
        verify(elasticsearchRepository)
                .findByQueryAndCategoryAndAvailableTrue(eq("phone"), eq("Electronics"), any(PageRequest.class));
    }

    @Test
    void shouldSearchByQueryAndPriceRange() {

        SearchCriteria criteria = new SearchCriteria("laptop", null, 500.0, 1500.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Budget Laptop", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"),
                        eq(new BigDecimal("500.0")),
                        eq(new BigDecimal("1500.0")),
                        any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"),
                        eq(new BigDecimal("500.0")),
                        eq(new BigDecimal("1500.0")),
                        any(PageRequest.class));
    }

    @Test
    void shouldSearchByQueryCategoryAndPriceRange() {

        SearchCriteria criteria = new SearchCriteria("gaming", "Electronics", 800.0, 2000.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Gaming PC", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndCategoryAndPriceRangeAndAvailableTrue(
                        eq("gaming"),
                        eq("Electronics"),
                        eq(new BigDecimal("800.0")),
                        eq(new BigDecimal("2000.0")),
                        any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        assertThat(result.products().getFirst().getName()).isEqualTo("Gaming PC");
        verify(elasticsearchRepository)
                .findByQueryAndCategoryAndPriceRangeAndAvailableTrue(
                        eq("gaming"),
                        eq("Electronics"),
                        eq(new BigDecimal("800.0")),
                        eq(new BigDecimal("2000.0")),
                        any(PageRequest.class));
    }

    @Test
    void shouldSearchWithoutQueryByCategoryOnly() {

        SearchCriteria criteria = new SearchCriteria(null, "Books", null, null, 0, 10);
        List<ProductDocument> documents = List.of(
                createProductDocument("1", "Java Guide", "Books"), createProductDocument("2", "Spring Boot", "Books"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 2);
        when(elasticsearchRepository.findByCategoryAndAvailableTrue(eq("Books"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(2);
        verify(elasticsearchRepository).findByCategoryAndAvailableTrue(eq("Books"), any(PageRequest.class));
    }

    @Test
    void shouldSearchWithoutQueryByPriceRangeOnly() {

        SearchCriteria criteria = new SearchCriteria(null, null, 10.0, 50.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Cheap Item", "Miscellaneous"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByPriceBetweenAndAvailableTrue(
                        eq(new BigDecimal("10.0")), eq(new BigDecimal("50.0")), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByPriceBetweenAndAvailableTrue(
                        eq(new BigDecimal("10.0")), eq(new BigDecimal("50.0")), any(PageRequest.class));
    }

    @Test
    void shouldSearchWithoutQueryByCategoryAndPriceRange() {

        SearchCriteria criteria = new SearchCriteria(null, "Electronics", 100.0, 300.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Tablet", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByCategoryAndPriceBetweenAndAvailableTrue(
                        eq("Electronics"),
                        eq(new BigDecimal("100.0")),
                        eq(new BigDecimal("300.0")),
                        any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByCategoryAndPriceBetweenAndAvailableTrue(
                        eq("Electronics"),
                        eq(new BigDecimal("100.0")),
                        eq(new BigDecimal("300.0")),
                        any(PageRequest.class));
    }

    @Test
    void shouldSearchAllAvailableProductsWithoutFilters() {

        SearchCriteria criteria = new SearchCriteria(null, null, null, null, 0, 10);
        List<ProductDocument> documents = List.of(
                createProductDocument("1", "Product 1", "Category1"),
                createProductDocument("2", "Product 2", "Category2"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 2);
        when(elasticsearchRepository.findByAvailableTrue(any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(2);
        verify(elasticsearchRepository).findByAvailableTrue(any(PageRequest.class));
    }

    @Test
    void shouldHandleEmptyQuery() {

        SearchCriteria criteria = new SearchCriteria("", "Electronics", null, null, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Electronic Device", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByCategoryAndAvailableTrue(eq("Electronics"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository).findByCategoryAndAvailableTrue(eq("Electronics"), any(PageRequest.class));
    }

    @Test
    void shouldHandleWhitespaceOnlyQuery() {

        SearchCriteria criteria = new SearchCriteria("   ", null, 10.0, 100.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Affordable Item", "Budget"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByPriceBetweenAndAvailableTrue(
                        eq(new BigDecimal("10.0")), eq(new BigDecimal("100.0")), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByPriceBetweenAndAvailableTrue(
                        eq(new BigDecimal("10.0")), eq(new BigDecimal("100.0")), any(PageRequest.class));
    }

    @Test
    void shouldHandleNullMinPrice() {

        SearchCriteria criteria = new SearchCriteria("laptop", null, null, 1000.0, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "Budget Laptop", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"), eq(BigDecimal.ZERO), eq(new BigDecimal("1000.0")), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"), eq(BigDecimal.ZERO), eq(new BigDecimal("1000.0")), any(PageRequest.class));
    }

    @Test
    void shouldHandleNullMaxPrice() {

        SearchCriteria criteria = new SearchCriteria("laptop", null, 500.0, null, 0, 10);
        List<ProductDocument> documents = List.of(createProductDocument("1", "High-end Laptop", "Electronics"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"),
                        eq(new BigDecimal("500.0")),
                        eq(BigDecimal.valueOf(Double.MAX_VALUE)),
                        any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        verify(elasticsearchRepository)
                .findByQueryAndPriceRangeAndAvailableTrue(
                        eq("laptop"),
                        eq(new BigDecimal("500.0")),
                        eq(BigDecimal.valueOf(Double.MAX_VALUE)),
                        any(PageRequest.class));
    }

    @Test
    void shouldHandlePagination() {

        SearchCriteria criteria = new SearchCriteria("product", null, null, null, 2, 5);
        List<ProductDocument> documents = List.of(
                createProductDocument("6", "Product 6", "Category"),
                createProductDocument("7", "Product 7", "Category"));

        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(2, 5), 20);
        when(elasticsearchRepository.findByQueryAndAvailableTrue(eq("product"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.currentPage()).isEqualTo(2);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(20);
        assertThat(result.totalPages()).isEqualTo(4);
        verify(elasticsearchRepository).findByQueryAndAvailableTrue("product", PageRequest.of(2, 5));
    }

    @Test
    void shouldReturnEmptyResultOnException() {

        SearchCriteria criteria = new SearchCriteria("error", null, null, null, 0, 10);
        when(elasticsearchRepository.findByQueryAndAvailableTrue(eq("error"), any(PageRequest.class)))
                .thenThrow(new RuntimeException("Elasticsearch connection error"));

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.isEmpty()).isTrue();
        assertThat(result.products()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void shouldConvertProductDocumentToDomain() {

        SearchCriteria criteria = new SearchCriteria("test", null, null, null, 0, 10);
        LocalDateTime now = LocalDateTime.now();

        ProductDocument document = new ProductDocument(
                "product-123",
                "Test Product",
                "Test Description",
                new BigDecimal("99.99"),
                "Electronics",
                15,
                now,
                now,
                true);

        List<ProductDocument> documents = List.of(document);
        Page<ProductDocument> page = new PageImpl<>(documents, PageRequest.of(0, 10), 1);
        when(elasticsearchRepository.findByQueryAndAvailableTrue(eq("test"), any(PageRequest.class)))
                .thenReturn(page);

        ProductSearchResult result = productSearchRepository.search(criteria);

        assertThat(result.products()).hasSize(1);
        ProductDomain convertedProduct = result.products().getFirst();
        assertThat(convertedProduct.getId().value()).isEqualTo("product-123");
        assertThat(convertedProduct.getName()).isEqualTo("Test Product");
        assertThat(convertedProduct.getDescription()).isEqualTo("Test Description");
        assertThat(convertedProduct.getPrice().value()).isEqualTo(new BigDecimal("99.99"));
        assertThat(convertedProduct.getCategory()).isEqualTo("Electronics");
        assertThat(convertedProduct.getStockQuantity()).isEqualTo(15);
        assertThat(convertedProduct.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void shouldThrowSearchExceptionWhenIndexFails() {

        ProductDomain product = createProductDomain(new BigDecimal("29.99"));
        RuntimeException underlyingException = new RuntimeException("Elasticsearch connection failed");

        when(elasticsearchRepository.save(any(ProductDocument.class))).thenThrow(underlyingException);

        assertThatThrownBy(() -> productSearchRepository.index(product))
                .isInstanceOf(SearchException.class)
                .hasMessage("Search operation 'index' failed for product product-1: Elasticsearch connection failed")
                .hasCause(underlyingException);

        verify(elasticsearchRepository).save(any(ProductDocument.class));
    }

    @Test
    void shouldThrowSearchExceptionWhenDeleteFails() {

        ProductDomain product = createProductDomain(new BigDecimal("29.99"));
        RuntimeException underlyingException = new RuntimeException("Elasticsearch delete operation failed");

        doThrow(underlyingException).when(elasticsearchRepository).deleteById("product-1");

        assertThatThrownBy(() -> productSearchRepository.delete(product))
                .isInstanceOf(SearchException.class)
                .hasMessage(
                        "Search operation 'delete' failed for product product-1: Elasticsearch delete operation failed")
                .hasCause(underlyingException);

        verify(elasticsearchRepository).deleteById("product-1");
    }

    private ProductDomain createProductDomain(BigDecimal price) {
        return new ProductDomain(
                ProductId.of("product-1"),
                "Test Product",
                "Description for " + "Test Product",
                Money.of(price),
                "Electronics",
                10,
                LocalDateTime.now());
    }

    private ProductDocument createProductDocument(String id, String name, String category) {
        LocalDateTime now = LocalDateTime.now();
        return new ProductDocument(
                id, name, "Description for " + name, new BigDecimal("29.99"), category, 10, now, now, true);
    }
}
