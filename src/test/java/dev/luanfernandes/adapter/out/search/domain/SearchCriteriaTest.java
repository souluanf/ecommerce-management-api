package dev.luanfernandes.adapter.out.search.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SearchCriteriaTest {

    @Test
    void shouldCreateSearchCriteriaWithAllParameters() {

        String query = "laptop";
        String category = "Electronics";
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("500.00");
        int page = 1;
        int size = 10;
        String sortField = "price";
        String sortDirection = "asc";

        SearchCriteria criteria =
                new SearchCriteria(query, category, minPrice, maxPrice, page, size, sortField, sortDirection);

        assertThat(criteria.query()).isEqualTo(query);
        assertThat(criteria.category()).isEqualTo(category);
        assertThat(criteria.minPrice()).isEqualTo(minPrice);
        assertThat(criteria.maxPrice()).isEqualTo(maxPrice);
        assertThat(criteria.page()).isEqualTo(page);
        assertThat(criteria.size()).isEqualTo(size);
        assertThat(criteria.sortField()).isEqualTo(sortField);
        assertThat(criteria.sortDirection()).isEqualTo(sortDirection);
    }

    @Test
    void shouldCreateSearchCriteriaWithNullValues() {

        SearchCriteria criteria = new SearchCriteria(null, null, null, null, 0, 20, null, null);

        assertThat(criteria.query()).isNull();
        assertThat(criteria.category()).isNull();
        assertThat(criteria.minPrice()).isNull();
        assertThat(criteria.maxPrice()).isNull();
        assertThat(criteria.page()).isZero();
        assertThat(criteria.size()).isEqualTo(20);
        assertThat(criteria.sortField()).isNull();
        assertThat(criteria.sortDirection()).isNull();
    }

    @Test
    void shouldCreateSearchCriteriaFromQueryOnly() {

        String query = "smartphone";

        SearchCriteria criteria = SearchCriteria.of(query);

        assertThat(criteria.query()).isEqualTo(query);
        assertThat(criteria.category()).isNull();
        assertThat(criteria.minPrice()).isNull();
        assertThat(criteria.maxPrice()).isNull();
        assertThat(criteria.page()).isZero();
        assertThat(criteria.size()).isEqualTo(20);
        assertThat(criteria.sortField()).isNull();
        assertThat(criteria.sortDirection()).isNull();
    }

    @Test
    void shouldCreateSearchCriteriaFromQueryWithPagination() {

        String query = "tablet";
        int page = 2;
        int size = 15;

        SearchCriteria criteria = SearchCriteria.of(query, page, size);

        assertThat(criteria.query()).isEqualTo(query);
        assertThat(criteria.category()).isNull();
        assertThat(criteria.minPrice()).isNull();
        assertThat(criteria.maxPrice()).isNull();
        assertThat(criteria.page()).isEqualTo(page);
        assertThat(criteria.size()).isEqualTo(size);
        assertThat(criteria.sortField()).isNull();
        assertThat(criteria.sortDirection()).isNull();
    }

    @Test
    void shouldCreateSearchCriteriaWithEmptyQuery() {

        String emptyQuery = "";

        SearchCriteria criteria = SearchCriteria.of(emptyQuery);

        assertThat(criteria.query()).isEqualTo(emptyQuery);
        assertThat(criteria.page()).isZero();
        assertThat(criteria.size()).isEqualTo(20);
    }

    @Test
    void shouldCreateSearchCriteriaWithZeroPagination() {

        SearchCriteria criteria = SearchCriteria.of("test", 0, 0);

        assertThat(criteria.query()).isEqualTo("test");
        assertThat(criteria.page()).isZero();
        assertThat(criteria.size()).isZero();
    }

    @Test
    void shouldHandleNullQueryInStaticMethod() {

        SearchCriteria criteria = SearchCriteria.of(null);

        assertThat(criteria.query()).isNull();
        assertThat(criteria.page()).isZero();
        assertThat(criteria.size()).isEqualTo(20);
    }

    @Test
    void shouldHandleNegativePagination() {

        SearchCriteria criteria = SearchCriteria.of("test", -1, -5);

        assertThat(criteria.query()).isEqualTo("test");
        assertThat(criteria.page()).isEqualTo(-1);
        assertThat(criteria.size()).isEqualTo(-5);
    }

    @Test
    void shouldEqualSearchCriteriaWithSameValues() {

        SearchCriteria criteria1 = new SearchCriteria(
                "laptop", "Electronics", new BigDecimal("100"), new BigDecimal("500"), 1, 10, "price", "asc");
        SearchCriteria criteria2 = new SearchCriteria(
                "laptop", "Electronics", new BigDecimal("100"), new BigDecimal("500"), 1, 10, "price", "asc");

        assertThat(criteria1).isEqualTo(criteria2).hasSameHashCodeAs(criteria2);
    }

    @Test
    void shouldNotEqualSearchCriteriaWithDifferentValues() {

        SearchCriteria criteria1 = SearchCriteria.of("laptop");
        SearchCriteria criteria2 = SearchCriteria.of("tablet");

        assertThat(criteria1).isNotEqualTo(criteria2);
    }

    @Test
    void shouldHaveToStringRepresentation() {

        SearchCriteria criteria = SearchCriteria.of("test", 1, 5);

        String toString = criteria.toString();

        assertThat(toString).contains("test").contains("1").contains("5");
    }
}
