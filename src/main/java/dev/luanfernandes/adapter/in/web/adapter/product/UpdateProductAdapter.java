package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.UpdateProductPort;
import dev.luanfernandes.application.usecase.product.UpdateProductUseCase;
import dev.luanfernandes.domain.dto.ProductResponse;
import dev.luanfernandes.domain.dto.UpdateProductRequest;
import dev.luanfernandes.domain.dto.command.UpdateProductCommand;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UpdateProductAdapter implements UpdateProductPort {

    private final UpdateProductUseCase updateProductUseCase;

    @Override
    public ResponseEntity<ProductResponse> updateProduct(UUID id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        var command = new UpdateProductCommand(
                ProductId.of(id),
                request.name(),
                request.description(),
                Money.of(request.price()),
                request.category(),
                request.stockQuantity());

        return updateProductUseCase
                .update(command)
                .map(product -> {
                    log.info("Product updated successfully: {}", id);
                    return ResponseEntity.ok(ProductResponse.from(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
