package dev.luanfernandes.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record StockUpdatedEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("updates") List<StockUpdateEvent> updates,
        @JsonProperty("processedAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime processedAt,
        @JsonProperty("status") String status) {
    public static StockUpdatedEvent success(String orderId, List<StockUpdateEvent> updates) {
        return new StockUpdatedEvent(
                java.util.UUID.randomUUID().toString(), orderId, updates, LocalDateTime.now(), "SUCCESS");
    }

    public static StockUpdatedEvent failed(String orderId, List<StockUpdateEvent> updates) {
        return new StockUpdatedEvent(
                java.util.UUID.randomUUID().toString(), orderId, updates, LocalDateTime.now(), "FAILED");
    }
}
