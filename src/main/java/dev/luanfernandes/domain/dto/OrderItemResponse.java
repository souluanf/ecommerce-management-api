package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.OrderItemDomain;
import java.math.BigDecimal;

public record OrderItemResponse(
        String productId, String productName, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
    public static OrderItemResponse from(OrderItemDomain item) {
        return new OrderItemResponse(
                item.getProductId().value(),
                item.getProductName(),
                item.getUnitPrice().value(),
                item.getQuantity(),
                item.getSubtotal().value());
    }
}
