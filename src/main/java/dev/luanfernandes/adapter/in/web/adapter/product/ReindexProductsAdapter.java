package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.ReindexProductsPort;
import dev.luanfernandes.application.usecase.product.ReindexProductsUseCase;
import dev.luanfernandes.domain.dto.ReindexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReindexProductsAdapter implements ReindexProductsPort {

    private final ReindexProductsUseCase reindexProductsUseCase;

    @Override
    public ResponseEntity<ReindexResponse> reindexAllProducts() {
        log.info("API: Reindex all products request");

        var result = reindexProductsUseCase.execute();

        log.info(
                "API: Reindex completed - Total: {}, Indexed: {}, Message: {}",
                result.totalProducts(),
                result.indexedProducts(),
                result.message());

        return ResponseEntity.ok(
                new ReindexResponse(result.totalProducts(), result.indexedProducts(), result.message()));
    }
}
