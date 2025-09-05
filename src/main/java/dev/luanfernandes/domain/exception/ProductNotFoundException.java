package dev.luanfernandes.domain.exception;

/**
 * Exception thrown when a product is not found.
 */
public class ProductNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";

    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 404; // NOT_FOUND
    }
}
