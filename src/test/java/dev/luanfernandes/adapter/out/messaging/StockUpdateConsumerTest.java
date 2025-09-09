package dev.luanfernandes.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.messaging.event.OrderFailedEvent;
import dev.luanfernandes.adapter.out.messaging.event.OrderItemEvent;
import dev.luanfernandes.adapter.out.messaging.event.OrderPaidEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdateEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdatedEvent;
import dev.luanfernandes.application.usecase.product.UpdateStockUseCase;
import dev.luanfernandes.domain.exception.StockUpdateException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class StockUpdateConsumerTest {

    @Mock
    private UpdateStockUseCase updateStockUseCase;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private Acknowledgment acknowledgment;

    @Captor
    private ArgumentCaptor<StockUpdatedEvent> stockUpdatedEventCaptor;

    @Captor
    private ArgumentCaptor<OrderFailedEvent> orderFailedEventCaptor;

    private StockUpdateConsumer stockUpdateConsumer;

    @BeforeEach
    void setUp() {
        stockUpdateConsumer = new StockUpdateConsumer(updateStockUseCase, kafkaTemplate);
    }

    @Test
    void shouldProcessOrderPaidEventSuccessfully() {

        OrderItemEvent item = OrderItemEvent.from("product-1", "Product 1", 2, new BigDecimal("29.99"));
        OrderPaidEvent event = OrderPaidEvent.create("order-123", "user-456", List.of(item), new BigDecimal("59.98"));

        List<StockUpdateEvent> stockUpdates = List.of(new StockUpdateEvent("product-1", 10, 8, 2));

        when(updateStockUseCase.updateStockFromOrder("order-123", List.of(item)))
                .thenReturn(stockUpdates);

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 123L, acknowledgment);

        verify(updateStockUseCase).updateStockFromOrder("order-123", List.of(item));
        verify(kafkaTemplate).send(eq("stock.updated"), eq("order-123"), stockUpdatedEventCaptor.capture());
        verify(acknowledgment).acknowledge();

        StockUpdatedEvent capturedEvent = stockUpdatedEventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo("order-123");
        assertThat(capturedEvent.status()).isEqualTo("SUCCESS");
        assertThat(capturedEvent.updates()).hasSize(1);
        assertThat(capturedEvent.updates().get(0).productId()).isEqualTo("product-1");
    }

    @Test
    void shouldIgnoreDuplicateEvents() {

        OrderItemEvent item = OrderItemEvent.from("product-1", "Product 1", 1, new BigDecimal("29.99"));
        OrderPaidEvent event = OrderPaidEvent.create("order-123", "user-456", List.of(item), new BigDecimal("29.99"));

        List<StockUpdateEvent> stockUpdates = List.of(new StockUpdateEvent("product-1", 10, 9, 1));

        when(updateStockUseCase.updateStockFromOrder("order-123", List.of(item)))
                .thenReturn(stockUpdates);

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 123L, acknowledgment);
        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 124L, acknowledgment);

        verify(updateStockUseCase, times(1)).updateStockFromOrder("order-123", List.of(item));
        verify(kafkaTemplate, times(1)).send(eq("stock.updated"), eq("order-123"), any(StockUpdatedEvent.class));
        verify(acknowledgment, times(2)).acknowledge();
    }

    @Test
    void shouldHandleStockUpdateFailure() {

        OrderItemEvent item = OrderItemEvent.from("product-1", "Product 1", 5, new BigDecimal("29.99"));
        OrderPaidEvent event = OrderPaidEvent.create("order-123", "user-456", List.of(item), new BigDecimal("149.95"));

        when(updateStockUseCase.updateStockFromOrder("order-123", List.of(item)))
                .thenThrow(new StockUpdateException("product-1", new RuntimeException("Insufficient stock")));

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 123L, acknowledgment);

        verify(updateStockUseCase).updateStockFromOrder("order-123", List.of(item));
        verify(kafkaTemplate, never()).send(eq("stock.updated"), any(), any());
        verify(kafkaTemplate).send(eq("order.failed.dlq"), eq("order-123"), orderFailedEventCaptor.capture());
        verify(acknowledgment).acknowledge();

        OrderFailedEvent failedEvent = orderFailedEventCaptor.getValue();
        assertThat(failedEvent.originalEventId()).isEqualTo(event.eventId());
        assertThat(failedEvent.orderId()).isEqualTo("order-123");
        assertThat(failedEvent.error()).contains("Failed to update stock for product: product-1");
        assertThat(failedEvent.retryCount()).isEqualTo(1);
    }

    @Test
    void shouldHandleMultipleItemsOrder() {

        OrderItemEvent item1 = OrderItemEvent.from("product-1", "Product 1", 2, new BigDecimal("29.99"));
        OrderItemEvent item2 = OrderItemEvent.from("product-2", "Product 2", 1, new BigDecimal("49.99"));

        OrderPaidEvent event =
                OrderPaidEvent.create("order-multi", "user-789", List.of(item1, item2), new BigDecimal("109.97"));

        List<StockUpdateEvent> stockUpdates =
                List.of(new StockUpdateEvent("product-1", 10, 8, 2), new StockUpdateEvent("product-2", 5, 4, 1));

        when(updateStockUseCase.updateStockFromOrder("order-multi", List.of(item1, item2)))
                .thenReturn(stockUpdates);

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 456L, acknowledgment);

        verify(updateStockUseCase).updateStockFromOrder("order-multi", List.of(item1, item2));
        verify(kafkaTemplate).send(eq("stock.updated"), eq("order-multi"), stockUpdatedEventCaptor.capture());
        verify(acknowledgment).acknowledge();

        StockUpdatedEvent capturedEvent = stockUpdatedEventCaptor.getValue();
        assertThat(capturedEvent.updates()).hasSize(2);
        assertThat(capturedEvent.updates().get(0).productId()).isEqualTo("product-1");
        assertThat(capturedEvent.updates().get(1).productId()).isEqualTo("product-2");
    }

    @Test
    void shouldHandleDeadLetterQueueEvent() {

        OrderFailedEvent failedEvent = OrderFailedEvent.create("event-123", "order-456", "Stock update failed", 1);

        stockUpdateConsumer.handleDeadLetterQueue(failedEvent, acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleDLQSendFailure() {

        OrderItemEvent item = OrderItemEvent.from("product-1", "Product 1", 1, new BigDecimal("29.99"));
        OrderPaidEvent event = OrderPaidEvent.create("order-123", "user-456", List.of(item), new BigDecimal("29.99"));

        when(updateStockUseCase.updateStockFromOrder("order-123", List.of(item)))
                .thenThrow(new StockUpdateException("product-1", new RuntimeException("Insufficient stock")));
        when(kafkaTemplate.send(eq("order.failed.dlq"), any(), any())).thenThrow(new RuntimeException("Kafka is down"));

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 123L, acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldHandleEmptyOrderItems() {

        OrderPaidEvent event = OrderPaidEvent.create("order-empty", "user-123", List.of(), BigDecimal.ZERO);

        when(updateStockUseCase.updateStockFromOrder("order-empty", List.of())).thenReturn(List.of());

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 789L, acknowledgment);

        verify(updateStockUseCase).updateStockFromOrder("order-empty", List.of());
        verify(kafkaTemplate).send(eq("stock.updated"), eq("order-empty"), stockUpdatedEventCaptor.capture());
        verify(acknowledgment).acknowledge();

        StockUpdatedEvent capturedEvent = stockUpdatedEventCaptor.getValue();
        assertThat(capturedEvent.updates()).isEmpty();
    }

    @Test
    void shouldHandleRuntimeExceptionDuringProcessing() {

        OrderItemEvent item = OrderItemEvent.from("product-1", "Product 1", 1, new BigDecimal("29.99"));
        OrderPaidEvent event = OrderPaidEvent.create("order-123", "user-456", List.of(item), new BigDecimal("29.99"));

        when(updateStockUseCase.updateStockFromOrder("order-123", List.of(item)))
                .thenThrow(new RuntimeException("Database connection error"));

        stockUpdateConsumer.handleOrderPaidEvent(event, "order.paid", 0, 123L, acknowledgment);

        verify(updateStockUseCase).updateStockFromOrder("order-123", List.of(item));
        verify(kafkaTemplate).send(eq("order.failed.dlq"), eq("order-123"), orderFailedEventCaptor.capture());
        verify(acknowledgment).acknowledge();

        OrderFailedEvent failedEvent = orderFailedEventCaptor.getValue();
        assertThat(failedEvent.error()).contains("Database connection error");
    }
}
