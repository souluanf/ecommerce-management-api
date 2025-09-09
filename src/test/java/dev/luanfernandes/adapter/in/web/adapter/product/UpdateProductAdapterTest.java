package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.product.UpdateProductUseCase;
import dev.luanfernandes.domain.dto.UpdateProductRequest;
import dev.luanfernandes.domain.dto.command.UpdateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
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
        classes = {UpdateProductAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class UpdateProductAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Product", "Updated Description", new BigDecimal("149.99"), "Updated Category", 75);

        ProductDomain updatedProduct = new ProductDomain(
                ProductId.of(productId),
                "Updated Product",
                "Updated Description",
                Money.of(new BigDecimal("149.99")),
                "Updated Category",
                75,
                LocalDateTime.now());

        when(updateProductUseCase.update(any(UpdateProductCommand.class))).thenReturn(Optional.of(updatedProduct));

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(149.99))
                .andExpect(jsonPath("$.category").value("Updated Category"))
                .andExpect(jsonPath("$.stockQuantity").value(75));
    }

    @Test
    void shouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        UUID productId = UUID.randomUUID();

        UpdateProductRequest request = new UpdateProductRequest(
                "Non-existent Product", "Description", new BigDecimal("50.00"), "Category", 10);

        when(updateProductUseCase.update(any(UpdateProductCommand.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidJson() throws Exception {
        UUID productId = UUID.randomUUID();
        String invalidJson = "{invalid json}";

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateProductWithPartialData() throws Exception {
        UUID productId = UUID.randomUUID();

        UpdateProductRequest request =
                new UpdateProductRequest("Partial Update", null, new BigDecimal("25.50"), "Books", 0);

        ProductDomain updatedProduct = new ProductDomain(
                ProductId.of(productId),
                "Partial Update",
                "",
                Money.of(new BigDecimal("25.50")),
                "Books",
                0,
                LocalDateTime.now());

        when(updateProductUseCase.update(any(UpdateProductCommand.class))).thenReturn(Optional.of(updatedProduct));

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(0));
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        UpdateProductRequest request =
                new UpdateProductRequest("Product", "Description", new BigDecimal("10.00"), "Category", 5);

        mockMvc.perform(put("/api/v1/products/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
