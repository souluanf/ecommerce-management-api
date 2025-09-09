package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class ProductInUseException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -4130435450169274123L;

    public ProductInUseException(String productId) {
        super("Cannot delete product that is associated with existing orders: " + productId);
    }

    @Override
    public String getErrorCode() {
        return "PRODUCT_IN_USE";
    }

    @Override
    public int getHttpStatusCode() {
        return 409;
    }
}
