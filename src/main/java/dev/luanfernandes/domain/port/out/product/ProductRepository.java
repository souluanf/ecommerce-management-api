package dev.luanfernandes.domain.port.out.product;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    ProductDomain save(ProductDomain product);

    Optional<ProductDomain> findById(ProductId id);

    List<ProductDomain> findAll();

    PageResponse<ProductDomain> findAllPaginated(PageRequest pageRequest);

    long countAll();

    void delete(ProductId id);

    boolean existsById(ProductId id);
}
