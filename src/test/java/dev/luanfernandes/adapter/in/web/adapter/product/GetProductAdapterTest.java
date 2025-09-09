package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.product.FindProductByIdUseCase;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        classes = {GetProductAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class GetProductAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindProductByIdUseCase findProductByIdUseCase;

    @Test
    void shouldGetProductSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Test Product",
                "Test Description",
                Money.of(new BigDecimal("99.99")),
                "Electronics",
                50,
                LocalDateTime.now());

        when(findProductByIdUseCase.execute(any(ProductId.class))).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/{id}", productId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.stockQuantity").value(50));
    }

    @Test
    void shouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        UUID productId = UUID.randomUUID();

        when(findProductByIdUseCase.execute(any(ProductId.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/{id}", productId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/api/v1/products/{id}", invalidId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProductWithZeroStock() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Out of Stock Product",
                "No stock available",
                Money.of(new BigDecimal("25.00")),
                "Books",
                0,
                LocalDateTime.now());

        when(findProductByIdUseCase.execute(any(ProductId.class))).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/{id}", productId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(0));
    }
}
