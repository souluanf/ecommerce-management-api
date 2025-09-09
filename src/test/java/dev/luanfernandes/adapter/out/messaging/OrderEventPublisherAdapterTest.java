package dev.luanfernandes.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.messaging.event.OrderPaidEvent;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.exception.EventPublicationException;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    @Mock
    private RecordMetadata recordMetadata;

    @Captor
    private ArgumentCaptor<OrderPaidEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    private OrderEventPublisherAdapter orderEventPublisher;

    @BeforeEach
    void setUp() {
        orderEventPublisher = new OrderEventPublisherAdapter(kafkaTemplate);
    }

    @Test
    void shouldPublishOrderPaidEventSuccessfully() {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("product-1"), "Test Product", Money.of(new BigDecimal("29.99")), 2);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-123"),
                UserId.of("user-456"),
                List.of(item),
                Money.of(new BigDecimal("59.98")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(123L);

        orderEventPublisher.publishOrderPaid(order);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("order.paid");
        assertThat(keyCaptor.getValue()).isEqualTo("order-123");

        OrderPaidEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo("order-123");
        assertThat(capturedEvent.userId()).isEqualTo("user-456");
        assertThat(capturedEvent.totalAmount()).isEqualTo(new BigDecimal("59.98"));
        assertThat(capturedEvent.items()).hasSize(1);
        assertThat(capturedEvent.items().get(0).productId()).isEqualTo("product-1");
        assertThat(capturedEvent.items().get(0).productName()).isEqualTo("Test Product");
        assertThat(capturedEvent.items().get(0).quantity()).isEqualTo(2);
        assertThat(capturedEvent.items().get(0).unitPrice()).isEqualTo(new BigDecimal("29.99"));
    }

    @Test
    void shouldHandleKafkaSendFailure() {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("product-1"), "Test Product", Money.of(new BigDecimal("29.99")), 1);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-123"),
                UserId.of("user-456"),
                List.of(item),
                Money.of(new BigDecimal("29.99")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka connection error"));

        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);

        assertThatThrownBy(() -> orderEventPublisher.publishOrderPaid(order))
                .isInstanceOf(EventPublicationException.class)
                .hasMessageContaining("Failed to publish OrderPaid event for entity order-123")
                .hasCauseInstanceOf(EventPublicationException.class);

        verify(kafkaTemplate).send(eq("order.paid"), eq("order-123"), any(OrderPaidEvent.class));
    }

    @Test
    void shouldHandleExceptionDuringEventCreation() {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("product-1"), "Test Product", Money.of(new BigDecimal("1.00")), 1);

        OrderDomain order = new OrderDomain(
                null,
                UserId.of("user-456"),
                List.of(item),
                Money.of(new BigDecimal("1.00")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        assertThatThrownBy(() -> orderEventPublisher.publishOrderPaid(order))
                .isInstanceOf(EventPublicationException.class)
                .hasMessageContaining("Failed to publish OrderPaid event")
                .hasCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldPublishOrderWithMultipleItems() {

        OrderItemDomain item1 =
                new OrderItemDomain(ProductId.of("product-1"), "Product 1", Money.of(new BigDecimal("10.00")), 1);

        OrderItemDomain item2 =
                new OrderItemDomain(ProductId.of("product-2"), "Product 2", Money.of(new BigDecimal("25.50")), 3);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-multi"),
                UserId.of("user-789"),
                List.of(item1, item2),
                Money.of(new BigDecimal("86.50")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(456L);

        orderEventPublisher.publishOrderPaid(order);

        verify(kafkaTemplate).send(eq("order.paid"), eq("order-multi"), eventCaptor.capture());

        OrderPaidEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.items()).hasSize(2);
        assertThat(capturedEvent.items().get(0).productId()).isEqualTo("product-1");
        assertThat(capturedEvent.items().get(0).quantity()).isEqualTo(1);
        assertThat(capturedEvent.items().get(1).productId()).isEqualTo("product-2");
        assertThat(capturedEvent.items().get(1).quantity()).isEqualTo(3);
        assertThat(capturedEvent.totalAmount()).isEqualTo(new BigDecimal("86.50"));
    }

    @Test
    void shouldGenerateUniqueEventIds() {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("product-1"), "Test Product", Money.of(new BigDecimal("29.99")), 1);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-123"),
                UserId.of("user-456"),
                List.of(item),
                Money.of(new BigDecimal("29.99")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(123L);

        orderEventPublisher.publishOrderPaid(order);
        orderEventPublisher.publishOrderPaid(order);

        verify(kafkaTemplate, times(2)).send(eq("order.paid"), eq("order-123"), eventCaptor.capture());

        List<OrderPaidEvent> capturedEvents = eventCaptor.getAllValues();
        assertThat(capturedEvents).hasSize(2);
        assertThat(capturedEvents.get(0).eventId())
                .isNotEqualTo(capturedEvents.get(1).eventId());
    }

    @Test
    void shouldHandleZeroAmountOrder() {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("free-product"), "Free Product", Money.of(BigDecimal.ZERO), 1);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-free"),
                UserId.of("user-123"),
                List.of(item),
                Money.of(BigDecimal.ZERO),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(789L);

        orderEventPublisher.publishOrderPaid(order);

        verify(kafkaTemplate).send(eq("order.paid"), eq("order-free"), eventCaptor.capture());

        OrderPaidEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(capturedEvent.items().get(0).unitPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldHandleInterruptedExceptionDuringKafkaSend() throws InterruptedException {

        OrderItemDomain item =
                new OrderItemDomain(ProductId.of("product-1"), "Test Product", Money.of(new BigDecimal("29.99")), 1);

        OrderDomain order = new OrderDomain(
                OrderId.of("order-123"),
                UserId.of("user-456"),
                List.of(item),
                Money.of(new BigDecimal("29.99")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(any(String.class), any(String.class), any(Object.class)))
                .thenReturn(future);

        Thread testThread = new Thread(() -> {
            assertThatThrownBy(() -> orderEventPublisher.publishOrderPaid(order))
                    .isInstanceOf(EventPublicationException.class)
                    .hasMessageContaining("Failed to publish OrderPaid event for entity order-123")
                    .satisfies(ex -> {
                        // The InterruptedException is wrapped twice, so we need to check the root cause
                        Throwable rootCause = ex.getCause();
                        while (rootCause != null && rootCause.getCause() != null) {
                            rootCause = rootCause.getCause();
                        }
                        assertThat(rootCause).isInstanceOf(InterruptedException.class);
                    });
        });

        testThread.start();
        Thread.sleep(100); // Give thread time to start and call future.get()
        testThread.interrupt(); // Interrupt the thread while it's waiting on future.get()
        testThread.join(1000); // Wait for thread to complete

        verify(kafkaTemplate).send(eq("order.paid"), eq("order-123"), any(OrderPaidEvent.class));
    }
}
