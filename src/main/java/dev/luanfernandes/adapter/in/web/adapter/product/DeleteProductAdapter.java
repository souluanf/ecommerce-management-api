package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.DeleteProductPort;
import dev.luanfernandes.application.usecase.product.DeleteProductUseCase;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeleteProductAdapter implements DeleteProductPort {

    private final DeleteProductUseCase deleteProductUseCase;

    @Override
    public ResponseEntity<Void> deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);

        try {
            deleteProductUseCase.delete(ProductId.of(id));
            log.info("Product deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Product not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Cannot delete product {}: {}", id, e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
