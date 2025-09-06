package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.exception.ProductInUseException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteProductUseCase.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductSearchRepository productSearchRepository;

    public DeleteProductUseCase(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productSearchRepository = productSearchRepository;
    }

    public void delete(ProductId productId) {
        log.info("🗑️ DeleteProduct: Attempting to delete product ID: {}", productId.value());

        if (!productRepository.existsById(productId)) {
            log.warn("DeleteProduct: Product not found - ID: {}", productId.value());
            throw new ProductNotFoundException(productId.value());
        }

        if (orderRepository.existsByProductId(productId)) {
            log.warn("⚠️ DeleteProduct: Cannot delete product associated with orders - ID: {}", productId.value());
            throw new ProductInUseException(productId.value());
        }

        var productToDelete = productRepository.findById(productId);

        productRepository.delete(productId);
        log.info("DeleteProduct: Product deleted from database - ID: {}", productId.value());

        if (productToDelete.isPresent()) {
            try {
                productSearchRepository.delete(productToDelete.get());
                log.info("🔍 DeleteProduct: Product removed from search index - ID: {}", productId.value());
            } catch (Exception e) {
                log.warn(
                        "DeleteProduct: Failed to remove product from search index - ID: {}, error: {}",
                        productId.value(),
                        e.getMessage());
            }
        }
        log.info("DeleteProduct: Product deletion completed - ID: {}", productId.value());
    }
}
