package dev.luanfernandes.adapter.out.persistence.adapter;

import dev.luanfernandes.adapter.out.persistence.mapper.ProductEntityMapper;
import dev.luanfernandes.adapter.out.persistence.repository.ProductJpaRepository;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository, ProductEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductDomain save(ProductDomain product) {
        var entity = mapper.toEntity(product);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ProductDomain> findById(ProductId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<ProductDomain> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsById(ProductId id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    public PageResponse<ProductDomain> findAllPaginated(PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);

        var page = jpaRepository.findAll(pageable);

        var content = page.getContent().stream().map(mapper::toDomain).toList();

        return PageResponse.of(pageRequest.pageNumber(), pageRequest.pageSize(), page.getTotalElements(), content);
    }

    @Override
    public long countAll() {
        return jpaRepository.count();
    }

    private Pageable createPageable(PageRequest pageRequest) {
        Sort sort = Sort.unsorted();

        if (pageRequest.hasSort()) {
            String[] sortParts = pageRequest.sort().split(",");
            String property = sortParts[0];
            Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, property);
        } else {
            // Default sort by name ascending
            sort = Sort.by(Sort.Direction.ASC, "name");
        }

        return org.springframework.data.domain.PageRequest.of(pageRequest.pageNumber(), pageRequest.pageSize(), sort);
    }
}
