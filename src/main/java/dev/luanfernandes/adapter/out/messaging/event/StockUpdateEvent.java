package dev.luanfernandes.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StockUpdateEvent(
        @JsonProperty("productId") String productId,
        @JsonProperty("previousStock") Integer previousStock,
        @JsonProperty("newStock") Integer newStock,
        @JsonProperty("quantityReduced") Integer quantityReduced) {
    public static StockUpdateEvent create(
            String productId, Integer previousStock, Integer newStock, Integer quantityReduced) {
        return new StockUpdateEvent(productId, previousStock, newStock, quantityReduced);
    }
}
