package dev.luanfernandes.domain.exception;

import java.io.Serial;

public class ProductNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";

    @Serial
    private static final long serialVersionUID = -393308434836741334L;

    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 404;
    }
}
