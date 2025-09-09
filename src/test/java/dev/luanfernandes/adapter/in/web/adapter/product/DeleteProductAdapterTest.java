package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.product.DeleteProductUseCase;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {DeleteProductAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class DeleteProductAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        doNothing().when(deleteProductUseCase).delete(any(ProductId.class));

        mockMvc.perform(delete("/api/v1/products/{id}", productId)).andExpect(status().isNoContent());

        verify(deleteProductUseCase).delete(any(ProductId.class));
    }

    @Test
    void shouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        UUID productId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Product not found"))
                .when(deleteProductUseCase)
                .delete(any(ProductId.class));

        mockMvc.perform(delete("/api/v1/products/{id}", productId)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnprocessableEntity_WhenProductInUse() throws Exception {
        UUID productId = UUID.randomUUID();

        doThrow(new IllegalStateException("Product is in use and cannot be deleted"))
                .when(deleteProductUseCase)
                .delete(any(ProductId.class));

        mockMvc.perform(delete("/api/v1/products/{id}", productId)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(delete("/api/v1/products/{id}", invalidId)).andExpect(status().isBadRequest());
    }
}
