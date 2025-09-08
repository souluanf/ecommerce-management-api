package dev.luanfernandes.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record OrderItemEvent(
        @JsonProperty("productId") String productId,
        @JsonProperty("productName") String productName,
        @JsonProperty("quantity") Integer quantity,
        @JsonProperty("unitPrice") BigDecimal unitPrice) {
    public static OrderItemEvent from(String productId, String productName, Integer quantity, BigDecimal unitPrice) {
        return new OrderItemEvent(productId, productName, quantity, unitPrice);
    }
}
