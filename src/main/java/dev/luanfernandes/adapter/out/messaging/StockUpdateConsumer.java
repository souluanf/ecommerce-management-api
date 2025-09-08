package dev.luanfernandes.adapter.out.messaging;

import dev.luanfernandes.adapter.out.messaging.event.OrderFailedEvent;
import dev.luanfernandes.adapter.out.messaging.event.OrderPaidEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdateEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdatedEvent;
import dev.luanfernandes.application.usecase.product.UpdateStockUseCase;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockUpdateConsumer.class);
    private static final String STOCK_UPDATED_TOPIC = "stock.updated";
    private static final String ORDER_FAILED_DLQ_TOPIC = "order.failed.dlq";

    private final UpdateStockUseCase updateStockUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Para garantir idempotência - armazenar eventos já processados
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    public StockUpdateConsumer(UpdateStockUseCase updateStockUseCase, KafkaTemplate<String, Object> kafkaTemplate) {
        this.updateStockUseCase = updateStockUseCase;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order.paid", groupId = "stock-update-consumer-group")
    public void handleOrderPaidEvent(
            @Payload OrderPaidEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info(
                "📨 Kafka: Received order paid event - eventId: {}, orderId: {}, topic: {}, partition: {}, offset: {}",
                event.eventId(),
                event.orderId(),
                topic,
                partition,
                offset);

        // Verificar idempotência
        if (processedEvents.contains(event.eventId())) {
            log.warn("⚠️ Kafka: Duplicate event ignored - eventId: {}, orderId: {}", event.eventId(), event.orderId());
            acknowledgment.acknowledge();
            return;
        }

        try {
            // Processar atualização de estoque
            List<StockUpdateEvent> updates = updateStockUseCase.updateStockFromOrder(event.orderId(), event.items());

            // Publicar evento de confirmação
            StockUpdatedEvent stockUpdatedEvent = StockUpdatedEvent.success(event.orderId(), updates);
            kafkaTemplate.send(STOCK_UPDATED_TOPIC, event.orderId(), stockUpdatedEvent);

            // Marcar como processado (idempotência)
            processedEvents.add(event.eventId());

            log.info(
                    "✅ Kafka: Stock updated successfully - eventId: {}, orderId: {}, updates: {}",
                    event.eventId(),
                    event.orderId(),
                    updates.size());

            // Confirmar processamento
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error(
                    "❌ Kafka: Failed to process order paid event - eventId: {}, orderId: {}, error: {}",
                    event.eventId(),
                    event.orderId(),
                    e.getMessage(),
                    e);

            // Enviar para Dead Letter Queue
            sendToDeadLetterQueue(event, e.getMessage(), 1);

            // Ainda assim fazer acknowledge para não reprocessar indefinidamente
            acknowledgment.acknowledge();
        }
    }

    private void sendToDeadLetterQueue(OrderPaidEvent originalEvent, String error, int retryCount) {
        try {
            OrderFailedEvent failedEvent =
                    OrderFailedEvent.create(originalEvent.eventId(), originalEvent.orderId(), error, retryCount);

            kafkaTemplate.send(ORDER_FAILED_DLQ_TOPIC, originalEvent.orderId(), failedEvent);

            log.error(
                    "💀 Kafka: Event sent to DLQ - eventId: {}, orderId: {}, error: {}, retryCount: {}",
                    originalEvent.eventId(),
                    originalEvent.orderId(),
                    error,
                    retryCount);

        } catch (Exception dlqError) {
            log.error(
                    "💀 Kafka: Failed to send event to DLQ - eventId: {}, orderId: {}, dlqError: {}",
                    originalEvent.eventId(),
                    originalEvent.orderId(),
                    dlqError.getMessage(),
                    dlqError);
        }
    }

    @KafkaListener(topics = "order.failed.dlq", groupId = "dlq-monitoring-group")
    public void handleDeadLetterQueue(@Payload OrderFailedEvent event, Acknowledgment acknowledgment) {

        log.error(
                "💀 DLQ: Processing failed event - eventId: {}, orderId: {}, error: '{}', retryCount: {}",
                event.eventId(),
                event.orderId(),
                event.error(),
                event.retryCount());

        // Aqui poderia implementar:
        // 1. Notificação para equipe de suporte
        // 2. Dashboard de monitoramento
        // 3. Tentativa manual de reprocessamento
        // 4. Armazenamento em banco para análise

        acknowledgment.acknowledge();
    }
}
