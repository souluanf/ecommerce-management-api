package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.order.FindOrdersByUserUseCase;
import dev.luanfernandes.application.usecase.order.ListAllOrdersUseCase;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
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
        classes = {GetAllOrdersAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class GetAllOrdersAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindOrdersByUserUseCase findOrdersByUserUseCase;

    @MockitoBean
    private ListAllOrdersUseCase listAllOrdersUseCase;

    @Test
    void shouldGetAllOrdersWithoutUserFilter() throws Exception {
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
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

        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 1);

        OrderDomain order1 = new OrderDomain(
                OrderId.of(orderId1),
                UserId.of(userId),
                List.of(orderItem),
                Money.of(new BigDecimal("100.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        OrderDomain order2 = new OrderDomain(
                OrderId.of(orderId2),
                UserId.of(userId),
                List.of(orderItem),
                Money.of(new BigDecimal("100.00")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        PageResponse<OrderDomain> pagedResult = PageResponse.of(0, 10, 2, List.of(order1, order2));

        when(listAllOrdersUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.elements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(listAllOrdersUseCase).execute(any(PageRequest.class));
        verify(findOrdersByUserUseCase, never()).execute(any(), any());
    }

    @Test
    void shouldGetOrdersByUser() throws Exception {
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
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        PageResponse<OrderDomain> pagedResult = PageResponse.of(0, 10, 1, List.of(order));

        when(findOrdersByUserUseCase.execute(any(UserId.class), any(PageRequest.class)))
                .thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.elements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(orderId.toString()))
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()));

        verify(findOrdersByUserUseCase).execute(any(UserId.class), any(PageRequest.class));
        verify(listAllOrdersUseCase, never()).execute(any());
    }

    @Test
    void shouldGetOrdersWithDateFilter() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        PageResponse<OrderDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllOrdersUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
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
    void shouldGetOrdersWithPagination() throws Exception {
        PageResponse<OrderDomain> pagedResult = PageResponse.of(2, 5, 0, List.of());

        when(listAllOrdersUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(2))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void shouldGetOrdersWithSorting() throws Exception {
        PageResponse<OrderDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllOrdersUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyListWhenNoOrders() throws Exception {
        PageResponse<OrderDomain> pagedResult = PageResponse.of(0, 10, 0, List.of());

        when(listAllOrdersUseCase.execute(any(PageRequest.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elements").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}
