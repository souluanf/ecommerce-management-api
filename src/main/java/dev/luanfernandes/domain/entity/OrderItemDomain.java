package dev.luanfernandes.domain.entity;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import lombok.Getter;

@Getter
public class OrderItemDomain {

    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;
    private final Money subtotal;

    public OrderItemDomain(ProductId productId, String productName, Money unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(quantity);
    }
}
