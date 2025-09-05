package dev.luanfernandes.domain.exception;

public class ProductInUseException extends BusinessException {

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
