package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.order.CancelOrderUseCase;
import dev.luanfernandes.domain.exception.InvalidOrderStatusException;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.valueobject.OrderId;
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
        classes = {CancelOrderAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class CancelOrderAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    void shouldCancelOrderSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();

        doNothing().when(cancelOrderUseCase).cancel(any(OrderId.class));

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)).andExpect(status().isNoContent());

        verify(cancelOrderUseCase).cancel(any(OrderId.class));
    }

    @Test
    void shouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();

        doThrow(new OrderNotFoundException(orderId)).when(cancelOrderUseCase).cancel(any(OrderId.class));

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenOrderCannotBeCancelled() throws Exception {
        UUID orderId = UUID.randomUUID();

        doThrow(new InvalidOrderStatusException("Order cannot be cancelled"))
                .when(cancelOrderUseCase)
                .cancel(any(OrderId.class));

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)).andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(delete("/api/v1/orders/{id}", invalidId)).andExpect(status().isBadRequest());
    }
}
