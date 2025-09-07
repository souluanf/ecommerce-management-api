package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.GetProductPort;
import dev.luanfernandes.application.usecase.product.FindProductByIdUseCase;
import dev.luanfernandes.domain.dto.ProductResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetProductAdapter implements GetProductPort {

    private final FindProductByIdUseCase findProductByIdUseCase;

    @Override
    public ResponseEntity<ProductResponse> getProduct(UUID id) {
        log.debug("Fetching product with ID: {}", id);

        ProductDomain product = findProductByIdUseCase.execute(ProductId.of(id));
        return ResponseEntity.ok(ProductResponse.from(product));
    }
}
