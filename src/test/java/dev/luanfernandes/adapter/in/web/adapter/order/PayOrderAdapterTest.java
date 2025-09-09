package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.order.PayOrderUseCase;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.InvalidOrderStatusException;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
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
        classes = {PayOrderAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class PayOrderAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayOrderUseCase payOrderUseCase;

    @Test
    void shouldPayOrderSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Test Product",
                "Description",
                Money.of(new BigDecimal("100.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 2);

        OrderDomain order = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(orderItem),
                Money.of(new BigDecimal("200.00")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        when(payOrderUseCase.execute(any(OrderId.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders/{id}/pay", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.totalAmount").value(200.00));
    }

    @Test
    void shouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(payOrderUseCase.execute(any(OrderId.class))).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(post("/api/v1/orders/{id}/pay", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenOrderAlreadyPaid() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(payOrderUseCase.execute(any(OrderId.class)))
                .thenThrow(new InvalidOrderStatusException("Order is already paid"));

        mockMvc.perform(post("/api/v1/orders/{id}/pay", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenOrderCancelled() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(payOrderUseCase.execute(any(OrderId.class)))
                .thenThrow(new InvalidOrderStatusException("Cannot pay cancelled order"));

        mockMvc.perform(post("/api/v1/orders/{id}/pay", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(post("/api/v1/orders/{id}/pay", invalidId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
