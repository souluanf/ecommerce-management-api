package dev.luanfernandes.adapter.in.web.adapter.product;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.product.ReindexProductsUseCase;
import dev.luanfernandes.domain.dto.result.ReindexResult;
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
        classes = {
            ReindexProductsAdapter.class,
            dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class ReindexProductsAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReindexProductsUseCase reindexProductsUseCase;

    @Test
    void shouldReindexProductsSuccessfully() throws Exception {
        ReindexResult reindexResult = new ReindexResult(100, 95, "Reindexing completed successfully");

        when(reindexProductsUseCase.execute()).thenReturn(reindexResult);

        mockMvc.perform(post("/api/v1/products/search/reindex").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProducts").value(100))
                .andExpect(jsonPath("$.indexedProducts").value(95))
                .andExpect(jsonPath("$.message").value("Reindexing completed successfully"));
    }

    @Test
    void shouldReindexWithPartialSuccess() throws Exception {
        ReindexResult reindexResult = new ReindexResult(50, 45, "5 products failed to index");

        when(reindexProductsUseCase.execute()).thenReturn(reindexResult);

        mockMvc.perform(post("/api/v1/products/search/reindex").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(50))
                .andExpect(jsonPath("$.indexedProducts").value(45))
                .andExpect(jsonPath("$.message").value("5 products failed to index"));
    }

    @Test
    void shouldReindexEmptyProducts() throws Exception {
        ReindexResult reindexResult = new ReindexResult(0, 0, "No products to index");

        when(reindexProductsUseCase.execute()).thenReturn(reindexResult);

        mockMvc.perform(post("/api/v1/products/search/reindex").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(0))
                .andExpect(jsonPath("$.indexedProducts").value(0))
                .andExpect(jsonPath("$.message").value("No products to index"));
    }
}
