package dev.luanfernandes.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "order_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String orderId;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public OrderItemJpaEntity() {}

    public OrderItemJpaEntity(
            String orderId,
            String productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal subtotal) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}
