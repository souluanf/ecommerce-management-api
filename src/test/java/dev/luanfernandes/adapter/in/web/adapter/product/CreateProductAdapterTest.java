package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.product.CreateProductUseCase;
import dev.luanfernandes.domain.dto.CreateProductRequest;
import dev.luanfernandes.domain.dto.command.CreateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
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
        classes = {CreateProductAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class CreateProductAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        CreateProductRequest request = new CreateProductRequest(
                "Test Product", "Test Description", new BigDecimal("99.99"), "Electronics", 100);

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Test Product",
                "Test Description",
                Money.of(new BigDecimal("99.99")),
                "Electronics",
                100,
                LocalDateTime.now());

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.stockQuantity").value(100));
    }

    @Test
    void shouldCreateProductWithMinimalValues() throws Exception {
        UUID productId = UUID.randomUUID();

        CreateProductRequest request = new CreateProductRequest(
                "Minimal Product", "Minimal Description", new BigDecimal("0.01"), "General", 0);

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Minimal Product",
                "Minimal Description",
                Money.of(new BigDecimal("0.01")),
                "General",
                0,
                LocalDateTime.now());

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(0.01))
                .andExpect(jsonPath("$.stockQuantity").value(0));
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidJson() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerError_WhenMissingRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }
}
