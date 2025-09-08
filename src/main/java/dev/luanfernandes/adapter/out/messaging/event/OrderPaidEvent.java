package dev.luanfernandes.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderPaidEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("userId") String userId,
        @JsonProperty("items") List<OrderItemEvent> items,
        @JsonProperty("totalAmount") BigDecimal totalAmount,
        @JsonProperty("paidAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime paidAt,
        @JsonProperty("timestamp") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime timestamp) {
    public static OrderPaidEvent create(
            String orderId, String userId, List<OrderItemEvent> items, BigDecimal totalAmount) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderPaidEvent(
                java.util.UUID.randomUUID().toString(), orderId, userId, items, totalAmount, now, now);
    }
}
