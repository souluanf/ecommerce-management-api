package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.adapter.out.search.domain.SearchCriteria;
import dev.luanfernandes.application.usecase.search.SearchProductsUseCase;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {SearchProductsAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class SearchProductsAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchProductsUseCase searchProductsUseCase;

    @Test
    void shouldSearchProductsSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Laptop Gaming",
                "High performance laptop",
                Money.of(new BigDecimal("999.99")),
                "Electronics",
                10,
                LocalDateTime.now());

        ProductSearchResult searchResult = ProductSearchResult.of(List.of(product), 1L, 1, 0, 10);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "laptop")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void shouldSearchProductsWithCategoryFilter() throws Exception {
        ProductSearchResult searchResult = ProductSearchResult.of(List.of(), 0L, 0, 0, 10);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "book")
                        .param("category", "Books")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(0));
    }

    @Test
    void shouldSearchProductsWithPriceRange() throws Exception {
        ProductSearchResult searchResult = ProductSearchResult.of(List.of(), 0L, 0, 0, 10);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "phone")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "500.00")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSearchProductsWithSorting() throws Exception {
        ProductSearchResult searchResult = ProductSearchResult.of(List.of(), 0L, 0, 0, 20);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "tablet")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortField", "price")
                        .param("sortDirection", "desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void shouldReturnEmptyResultsWhenNoMatches() throws Exception {
        ProductSearchResult searchResult = ProductSearchResult.of(List.of(), 0L, 0, 0, 10);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "nonexistent")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(0));
    }

    @Test
    void shouldSearchWithMultipleFilters() throws Exception {
        ProductSearchResult searchResult = ProductSearchResult.of(List.of(), 0L, 0, 0, 5);

        when(searchProductsUseCase.execute(any(SearchCriteria.class))).thenReturn(searchResult);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "gaming")
                        .param("category", "Electronics")
                        .param("minPrice", "500.00")
                        .param("maxPrice", "2000.00")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortField", "name")
                        .param("sortDirection", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
