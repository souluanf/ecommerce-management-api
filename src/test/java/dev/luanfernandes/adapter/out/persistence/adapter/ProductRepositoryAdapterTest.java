package dev.luanfernandes.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.persistence.entity.ProductJpaEntity;
import dev.luanfernandes.adapter.out.persistence.mapper.ProductEntityMapper;
import dev.luanfernandes.adapter.out.persistence.repository.ProductJpaRepository;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ProductRepositoryAdapter")
class ProductRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @Mock
    private ProductEntityMapper mapper;

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProduct_Successfully() {
        ProductDomain productDomain = createProductDomain("Test Product");
        ProductJpaEntity productEntity = createProductEntity("Test Product");
        ProductDomain savedProductDomain = createProductDomain("Test Product");

        when(mapper.toEntity(productDomain)).thenReturn(productEntity);
        when(jpaRepository.save(productEntity)).thenReturn(productEntity);
        when(mapper.toDomain(productEntity)).thenReturn(savedProductDomain);

        ProductDomain result = productRepositoryAdapter.save(productDomain);

        assertThat(result).isNotNull().isEqualTo(savedProductDomain);

        verify(mapper).toEntity(productDomain);
        verify(jpaRepository).save(productEntity);
        verify(mapper).toDomain(productEntity);
    }

    @Test
    @DisplayName("Should find product by id when product exists")
    void shouldFindProductById_WhenProductExists() {
        ProductId productId = ProductId.generate();
        ProductJpaEntity productEntity = createProductEntity("Test Product");
        ProductDomain productDomain = createProductDomain("Test Product");

        when(jpaRepository.findById(productId.value())).thenReturn(Optional.of(productEntity));
        when(mapper.toDomain(productEntity)).thenReturn(productDomain);

        Optional<ProductDomain> result = productRepositoryAdapter.findById(productId);

        assertThat(result).isPresent().hasValueSatisfying(product -> assertThat(product)
                .isEqualTo(productDomain));

        verify(jpaRepository).findById(productId.value());
        verify(mapper).toDomain(productEntity);
    }

    @Test
    @DisplayName("Should return empty when product not found by id")
    void shouldReturnEmpty_WhenProductNotFoundById() {
        ProductId productId = ProductId.generate();

        when(jpaRepository.findById(productId.value())).thenReturn(Optional.empty());

        Optional<ProductDomain> result = productRepositoryAdapter.findById(productId);

        assertThat(result).isEmpty();

        verify(jpaRepository).findById(productId.value());
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        List<ProductJpaEntity> entities = Arrays.asList(
                createProductEntity("Product 1"), createProductEntity("Product 2"), createProductEntity("Product 3"));
        List<ProductDomain> domains = Arrays.asList(
                createProductDomain("Product 1"), createProductDomain("Product 2"), createProductDomain("Product 3"));

        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));
        when(mapper.toDomain(entities.get(2))).thenReturn(domains.get(2));

        List<ProductDomain> result = productRepositoryAdapter.findAll();

        assertThat(result).hasSize(3).containsExactlyElementsOf(domains);

        verify(jpaRepository).findAll();
        verify(mapper).toDomain(entities.get(0));
        verify(mapper).toDomain(entities.get(1));
        verify(mapper).toDomain(entities.get(2));
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyList_WhenNoProductsExist() {
        when(jpaRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductDomain> result = productRepositoryAdapter.findAll();

        assertThat(result).isEmpty();

        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Should delete product by id")
    void shouldDeleteProduct_ById() {
        ProductId productId = ProductId.generate();

        productRepositoryAdapter.delete(productId);

        verify(jpaRepository).deleteById(productId.value());
    }

    @Test
    @DisplayName("Should return true when product exists by id")
    void shouldReturnTrue_WhenProductExistsById() {
        ProductId productId = ProductId.generate();

        when(jpaRepository.existsById(productId.value())).thenReturn(true);

        boolean result = productRepositoryAdapter.existsById(productId);

        assertThat(result).isTrue();

        verify(jpaRepository).existsById(productId.value());
    }

    @Test
    @DisplayName("Should return false when product does not exist by id")
    void shouldReturnFalse_WhenProductDoesNotExistById() {
        ProductId productId = ProductId.generate();

        when(jpaRepository.existsById(productId.value())).thenReturn(false);

        boolean result = productRepositoryAdapter.existsById(productId);

        assertThat(result).isFalse();

        verify(jpaRepository).existsById(productId.value());
    }

    @Test
    @DisplayName("Should find all products paginated")
    void shouldFindAllProducts_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10, "name,asc");
        List<ProductJpaEntity> entities =
                Arrays.asList(createProductEntity("Product 1"), createProductEntity("Product 2"));
        List<ProductDomain> domains = Arrays.asList(createProductDomain("Product 1"), createProductDomain("Product 2"));
        Page<ProductJpaEntity> page =
                new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 2);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(2).containsExactlyElementsOf(domains);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.elements()).isEqualTo(2);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find all products paginated with no sort")
    void shouldFindAllProducts_PaginatedWithNoSort() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<ProductJpaEntity> entities = Collections.singletonList(createProductEntity("Product 1"));
        List<ProductDomain> domains = Collections.singletonList(createProductDomain("Product 1"));
        Page<ProductJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 5), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(domains.getFirst());

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(1);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(5);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find all products paginated with descending sort")
    void shouldFindAllProducts_PaginatedWithDescendingSort() {
        PageRequest pageRequest = PageRequest.of(1, 5, "createdAt,desc");
        List<ProductJpaEntity> entities =
                Arrays.asList(createProductEntity("Product 1"), createProductEntity("Product 2"));
        List<ProductDomain> domains = Arrays.asList(createProductDomain("Product 1"), createProductDomain("Product 2"));
        Page<ProductJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(1, 5), 7);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(2);
            assertThat(response.pageNumber()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(5);
            assertThat(response.elements()).isEqualTo(7);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return total count of all products")
    void shouldReturnTotalCount_OfAllProducts() {
        when(jpaRepository.count()).thenReturn(42L);

        long result = productRepositoryAdapter.countAll();

        assertThat(result).isEqualTo(42L);

        verify(jpaRepository).count();
    }

    @Test
    @DisplayName("Should return zero count when no products exist")
    void shouldReturnZeroCount_WhenNoProductsExist() {
        when(jpaRepository.count()).thenReturn(0L);

        long result = productRepositoryAdapter.countAll();

        assertThat(result).isZero();

        verify(jpaRepository).count();
    }

    @Test
    @DisplayName("Should handle large number of products in findAll")
    void shouldHandleLargeNumberOfProducts_InFindAll() {
        List<ProductJpaEntity> entities = Collections.nCopies(1000, createProductEntity("Product"));
        List<ProductDomain> domains = Collections.nCopies(1000, createProductDomain("Product"));

        when(jpaRepository.findAll()).thenReturn(entities);
        for (int i = 0; i < entities.size(); i++) {
            when(mapper.toDomain(entities.get(i))).thenReturn(domains.get(i));
        }

        List<ProductDomain> result = productRepositoryAdapter.findAll();

        assertThat(result).hasSize(1000);

        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Should properly delegate to mapper for entity conversion")
    void shouldProperlyDelegate_ToMapperForEntityConversion() {
        ProductDomain productDomain = createProductDomain("Mapper Test");
        ProductJpaEntity productEntity = createProductEntity("Mapper Test");

        when(mapper.toEntity(productDomain)).thenReturn(productEntity);
        when(jpaRepository.save(any(ProductJpaEntity.class))).thenReturn(productEntity);
        when(mapper.toDomain(productEntity)).thenReturn(productDomain);

        productRepositoryAdapter.save(productDomain);

        verify(mapper).toEntity(productDomain);
        verify(mapper).toDomain(productEntity);
    }

    @Test
    @DisplayName("Should handle empty paginated results")
    void shouldHandleEmpty_PaginatedResults() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductJpaEntity> emptyPage =
                new PageImpl<>(Collections.emptyList(), org.springframework.data.domain.PageRequest.of(0, 10), 0);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).isEmpty();
            assertThat(response.elements()).isZero();
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(10);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create pageable with ascending sort when direction not specified")
    void shouldCreatePageable_WithAscendingSortWhenDirectionNotSpecified() {
        PageRequest pageRequest = PageRequest.of(0, 10, "price");
        List<ProductJpaEntity> entities = Collections.singletonList(createProductEntity("Product"));
        List<ProductDomain> domains = Collections.singletonList(createProductDomain("Product"));
        Page<ProductJpaEntity> page =
                new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should use default name sort when no sort provided")
    void shouldUseDefaultNameSort_WhenNoSortProvided() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<ProductJpaEntity> entities = Collections.singletonList(createProductEntity("Test Product"));
        List<ProductDomain> domains = Collections.singletonList(createProductDomain("Test Product"));
        Page<ProductJpaEntity> page =
                new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(1, 5), 10);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));

        PageResponse<ProductDomain> result = productRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(1);
            assertThat(response.pageNumber()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(5);
            assertThat(response.elements()).isEqualTo(10);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    private ProductDomain createProductDomain(String name) {
        return new ProductDomain(
                ProductId.generate(),
                name,
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                10,
                LocalDateTime.now());
    }

    private ProductJpaEntity createProductEntity(String name) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(ProductId.generate().value());
        entity.setName(name);
        entity.setDescription("Test Description");
        entity.setPrice(new BigDecimal("99.99"));
        entity.setCategory("ELECTRONICS");
        entity.setStockQuantity(10);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
