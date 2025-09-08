package dev.luanfernandes.adapter.out.messaging;

import dev.luanfernandes.adapter.out.messaging.event.OrderItemEvent;
import dev.luanfernandes.adapter.out.messaging.event.OrderPaidEvent;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderEventPublisher;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisherAdapter implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisherAdapter.class);
    private static final String ORDER_PAID_TOPIC = "order.paid";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderPaid(OrderDomain order) {
        try {
            OrderPaidEvent event = createOrderPaidEvent(order);

            log.info(
                    "📨 Kafka: Publishing order paid event - orderId: {}, eventId: {}",
                    order.getId().value(),
                    event.eventId());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(ORDER_PAID_TOPIC, order.getId().value(), event);

            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error(
                            "❌ Kafka: Failed to publish order paid event - orderId: {}, eventId: {}, error: {}",
                            order.getId().value(),
                            event.eventId(),
                            throwable.getMessage(),
                            throwable);
                } else {
                    log.info(
                            "✅ Kafka: Order paid event published successfully - orderId: {}, eventId: {}, offset: {}",
                            order.getId().value(),
                            event.eventId(),
                            result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            log.error(
                    "❌ Kafka: Error creating order paid event - orderId: {}, error: {}",
                    order.getId().value(),
                    e.getMessage(),
                    e);
            throw new RuntimeException("Failed to publish order paid event", e);
        }
    }

    private OrderPaidEvent createOrderPaidEvent(OrderDomain order) {
        var itemEvents = order.getItems().stream()
                .map(item -> OrderItemEvent.from(
                        item.getProductId().value(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice().value()))
                .toList();

        return OrderPaidEvent.create(
                order.getId().value(),
                order.getUserId().value(),
                itemEvents,
                order.getTotalAmount().value());
    }
}
