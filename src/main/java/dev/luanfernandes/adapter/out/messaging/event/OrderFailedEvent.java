package dev.luanfernandes.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record OrderFailedEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("originalEventId") String originalEventId,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("error") String error,
        @JsonProperty("retryCount") Integer retryCount,
        @JsonProperty("failedAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime failedAt) {
    public static OrderFailedEvent create(String originalEventId, String orderId, String error, Integer retryCount) {
        return new OrderFailedEvent(
                java.util.UUID.randomUUID().toString(),
                originalEventId,
                orderId,
                error,
                retryCount,
                LocalDateTime.now());
    }
}
