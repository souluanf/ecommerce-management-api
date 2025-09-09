package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.product.ListAllProductsUseCase;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        classes = {GetAllProductsAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class GetAllProductsAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListAllProductsUseCase listAllProductsUseCase;

    @Test
    void shouldGetAllProductsSuccessfully() throws Exception {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        ProductDomain product1 = new ProductDomain(
                ProductId.of(productId1),
                "Product 1",
                "Description 1",
                Money.of(new BigDecimal("99.99")),
                "Electronics",
                10,
                LocalDateTime.now());

        ProductDomain product2 = new ProductDomain(
                ProductId.of(productId2),
                "Product 2",
                "Description 2",
                Money.of(new BigDecimal("49.99")),
                "Books",
                5,
                LocalDateTime.now());

        PageResponse<ProductDomain> pagedResult = PageResponse.of(0, 10, 2, List.of(product1, product2));

        when(listAllProductsUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.elements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(productId1.toString()))
                .andExpect(jsonPath("$.content[1].id").value(productId2.toString()));
    }

    @Test
    void shouldGetProductsWithDateFilter() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        PageResponse<ProductDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllProductsUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void shouldGetProductsWithPagination() throws Exception {
        PageResponse<ProductDomain> pagedResult = PageResponse.of(2, 5, 0, List.of());

        when(listAllProductsUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(2))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void shouldGetProductsWithSorting() throws Exception {
        PageResponse<ProductDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllProductsUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() throws Exception {
        PageResponse<ProductDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllProductsUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elements").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}
