package dev.luanfernandes.adapter.out.search.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchResultTest {

    @Test
    void shouldCreateSearchResultWithProducts() {

        List<ProductDocument> products =
                List.of(createProductDocument("1", "Product 1"), createProductDocument("2", "Product 2"));

        SearchResult result = SearchResult.of(products, 2, 1, 0, 10);

        assertThat(result.products()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.currentPage()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void shouldCreateEmptySearchResult() {

        SearchResult result = SearchResult.empty();

        assertThat(result.products()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
        assertThat(result.currentPage()).isZero();
        assertThat(result.pageSize()).isEqualTo(20);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.size()).isZero();
    }

    @Test
    void shouldCalculateHasNextCorrectly() {
        List<ProductDocument> products = List.of(createProductDocument("1", "Product 1"));

        SearchResult result = SearchResult.of(products, 25, 3, 0, 10);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    void shouldCalculateHasPreviousCorrectly() {
        List<ProductDocument> products = List.of(createProductDocument("1", "Product 1"));

        SearchResult result = SearchResult.of(products, 25, 3, 1, 10);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void shouldCalculateLastPageCorrectly() {
        List<ProductDocument> products = List.of(createProductDocument("1", "Product 1"));

        SearchResult result = SearchResult.of(products, 25, 3, 2, 10);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void shouldHandleSinglePageResult() {

        List<ProductDocument> products =
                List.of(createProductDocument("1", "Product 1"), createProductDocument("2", "Product 2"));

        SearchResult result = SearchResult.of(products, 2, 1, 0, 10);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    void shouldHandleExactlyOnePageFull() {
        List<ProductDocument> products = List.of(
                createProductDocument("1", "Product 1"),
                createProductDocument("2", "Product 2"),
                createProductDocument("3", "Product 3"),
                createProductDocument("4", "Product 4"),
                createProductDocument("5", "Product 5"),
                createProductDocument("6", "Product 6"),
                createProductDocument("7", "Product 7"),
                createProductDocument("8", "Product 8"),
                createProductDocument("9", "Product 9"),
                createProductDocument("10", "Product 10"));

        SearchResult result = SearchResult.of(products, 10, 1, 0, 10);

        assertThat(result.size()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    void shouldHandleZeroTotalElements() {

        List<ProductDocument> products = List.of();

        SearchResult result = SearchResult.of(products, 0, 0, 0, 10);

        assertThat(result.isEmpty()).isTrue();
        assertThat(result.size()).isZero();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    void shouldHandleDifferentPageSizes() {

        List<ProductDocument> products = List.of(
                createProductDocument("1", "Product 1"),
                createProductDocument("2", "Product 2"),
                createProductDocument("3", "Product 3"));

        SearchResult result = SearchResult.of(products, 3, 1, 0, 5);

        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void shouldMaintainProductOrder() {

        ProductDocument product1 = createProductDocument("1", "Alpha Product");
        ProductDocument product2 = createProductDocument("2", "Beta Product");
        ProductDocument product3 = createProductDocument("3", "Gamma Product");

        List<ProductDocument> products = List.of(product1, product2, product3);

        SearchResult result = SearchResult.of(products, 3, 1, 0, 10);

        assertThat(result.products()).containsExactly(product1, product2, product3);
    }

    private ProductDocument createProductDocument(String id, String name) {
        LocalDateTime now = LocalDateTime.now();
        return new ProductDocument(
                id, name, "Test description for " + name, new BigDecimal("29.99"), "Electronics", 10, now, now, true);
    }
}
