package dev.luanfernandes.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.persistence.entity.OrderJpaEntity;
import dev.luanfernandes.adapter.out.persistence.mapper.OrderEntityMapper;
import dev.luanfernandes.adapter.out.persistence.repository.OrderJpaRepository;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@DisplayName("Tests for OrderRepositoryAdapter")
class OrderRepositoryAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderEntityMapper mapper;

    @InjectMocks
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Test
    @DisplayName("Should save new order successfully")
    void shouldSaveNewOrder_Successfully() {
        OrderDomain orderDomain = createOrderDomain(OrderDomain.OrderStatus.PENDING);
        OrderJpaEntity orderEntity = createOrderEntity();
        OrderDomain savedOrderDomain = createOrderDomain(OrderDomain.OrderStatus.PENDING);

        when(jpaRepository.findById(orderDomain.getId().value())).thenReturn(Optional.empty());
        when(mapper.toEntity(orderDomain)).thenReturn(orderEntity);
        when(jpaRepository.save(orderEntity)).thenReturn(orderEntity);
        when(mapper.toDomain(orderEntity)).thenReturn(savedOrderDomain);

        OrderDomain result = orderRepositoryAdapter.save(orderDomain);

        assertThat(result).isNotNull().isEqualTo(savedOrderDomain);

        verify(jpaRepository).findById(orderDomain.getId().value());
        verify(mapper).toEntity(orderDomain);
        verify(jpaRepository).save(orderEntity);
        verify(mapper).toDomain(orderEntity);
    }

    @Test
    @DisplayName("Should update existing order successfully")
    void shouldUpdateExistingOrder_Successfully() {
        OrderDomain orderDomain = createOrderDomain(OrderDomain.OrderStatus.PAID);
        OrderJpaEntity existingEntity = createOrderEntity();
        OrderJpaEntity newEntity = createOrderEntity();
        OrderDomain savedOrderDomain = createOrderDomain(OrderDomain.OrderStatus.PAID);

        when(jpaRepository.findById(orderDomain.getId().value())).thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(orderDomain)).thenReturn(newEntity);
        when(jpaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toDomain(existingEntity)).thenReturn(savedOrderDomain);

        OrderDomain result = orderRepositoryAdapter.save(orderDomain);

        assertThat(result).isNotNull().isEqualTo(savedOrderDomain);

        verify(jpaRepository).findById(orderDomain.getId().value());
        verify(mapper).toEntity(orderDomain);
        verify(jpaRepository).save(existingEntity);
        verify(mapper).toDomain(existingEntity);

        assertThat(existingEntity.getStatus()).isEqualTo(newEntity.getStatus());
        assertThat(existingEntity.getTotalAmount()).isEqualTo(newEntity.getTotalAmount());
        assertThat(existingEntity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find order by id when order exists")
    void shouldFindOrderById_WhenOrderExists() {
        OrderId orderId = OrderId.generate();
        OrderJpaEntity orderEntity = createOrderEntity();
        OrderDomain orderDomain = createOrderDomain(OrderDomain.OrderStatus.PENDING);

        when(jpaRepository.findById(orderId.value())).thenReturn(Optional.of(orderEntity));
        when(mapper.toDomain(orderEntity)).thenReturn(orderDomain);

        Optional<OrderDomain> result = orderRepositoryAdapter.findById(orderId);

        assertThat(result).isPresent().hasValueSatisfying(order -> assertThat(order)
                .isEqualTo(orderDomain));

        verify(jpaRepository).findById(orderId.value());
        verify(mapper).toDomain(orderEntity);
    }

    @Test
    @DisplayName("Should return empty when order not found by id")
    void shouldReturnEmpty_WhenOrderNotFoundById() {
        OrderId orderId = OrderId.generate();

        when(jpaRepository.findById(orderId.value())).thenReturn(Optional.empty());

        Optional<OrderDomain> result = orderRepositoryAdapter.findById(orderId);

        assertThat(result).isEmpty();

        verify(jpaRepository).findById(orderId.value());
    }

    @Test
    @DisplayName("Should find orders by user id")
    void shouldFindOrdersByUserId() {
        UserId userId = UserId.generate();
        List<OrderJpaEntity> entities = Arrays.asList(createOrderEntity(), createOrderEntity());
        List<OrderDomain> domains = Arrays.asList(
                createOrderDomain(OrderDomain.OrderStatus.PENDING), createOrderDomain(OrderDomain.OrderStatus.PAID));

        when(jpaRepository.findByUserId(userId.value())).thenReturn(entities);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        List<OrderDomain> result = orderRepositoryAdapter.findByUserId(userId);

        assertThat(result).hasSize(2).containsExactlyElementsOf(domains);

        verify(jpaRepository).findByUserId(userId.value());
    }

    @Test
    @DisplayName("Should find orders by date range")
    void shouldFindOrdersByDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        List<OrderDomain> domains = Collections.singletonList(createOrderDomain(OrderDomain.OrderStatus.PAID));

        when(jpaRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(entities);
        when(mapper.toDomain(entities.getFirst())).thenReturn(domains.getFirst());

        List<OrderDomain> result = orderRepositoryAdapter.findByDateRange(startDate, endDate);

        assertThat(result).hasSize(1).containsExactlyElementsOf(domains);

        verify(jpaRepository).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should find paid orders by date range")
    void shouldFindPaidOrdersByDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        List<OrderDomain> domains = Collections.singletonList(createOrderDomain(OrderDomain.OrderStatus.PAID));

        when(jpaRepository.findPaidOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(entities);
        when(mapper.toDomain(entities.getFirst())).thenReturn(domains.getFirst());

        List<OrderDomain> result = orderRepositoryAdapter.findPaidOrdersByDateRange(startDate, endDate);

        assertThat(result).hasSize(1).containsExactlyElementsOf(domains);

        verify(jpaRepository).findPaidOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should check if orders exist by product id")
    void shouldCheckIfOrdersExist_ByProductId() {
        ProductId productId = ProductId.generate();

        when(jpaRepository.existsByProductId(productId.value())).thenReturn(true);

        boolean result = orderRepositoryAdapter.existsByProductId(productId);

        assertThat(result).isTrue();

        verify(jpaRepository).existsByProductId(productId.value());
    }

    @Test
    @DisplayName("Should find all orders")
    void shouldFindAllOrders() {
        List<OrderJpaEntity> entities = Arrays.asList(createOrderEntity(), createOrderEntity(), createOrderEntity());
        List<OrderDomain> domains = Arrays.asList(
                createOrderDomain(OrderDomain.OrderStatus.PENDING),
                createOrderDomain(OrderDomain.OrderStatus.PAID),
                createOrderDomain(OrderDomain.OrderStatus.CANCELLED));

        when(jpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));
        when(mapper.toDomain(entities.get(2))).thenReturn(domains.get(2));

        List<OrderDomain> result = orderRepositoryAdapter.findAll();

        assertThat(result).hasSize(3).containsExactlyElementsOf(domains);

        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Should find all orders paginated")
    void shouldFindAllOrders_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt,desc");
        List<OrderJpaEntity> entities = Arrays.asList(createOrderEntity(), createOrderEntity());
        List<OrderDomain> domains = Arrays.asList(
                createOrderDomain(OrderDomain.OrderStatus.PENDING), createOrderDomain(OrderDomain.OrderStatus.PAID));
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 2);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(mapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(2).containsExactlyElementsOf(domains);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(10);
        });

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find orders by user id paginated")
    void shouldFindOrdersByUserId_Paginated() {
        UserId userId = UserId.generate();
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        List<OrderDomain> domains = Collections.singletonList(createOrderDomain(OrderDomain.OrderStatus.PENDING));
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 5), 1);

        when(jpaRepository.findByUserIdOrderByCreatedAtDesc(eq(userId.value()), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(domains.getFirst());

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findByUserIdPaginated(userId, pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).hasSize(1);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(5);
        });

        verify(jpaRepository).findByUserIdOrderByCreatedAtDesc(eq(userId.value()), any(Pageable.class));
    }

    @Test
    @DisplayName("Should count all orders")
    void shouldCountAllOrders() {
        when(jpaRepository.count()).thenReturn(25L);

        long result = orderRepositoryAdapter.countAll();

        assertThat(result).isEqualTo(25L);

        verify(jpaRepository).count();
    }

    @Test
    @DisplayName("Should count orders by user id")
    void shouldCountOrdersByUserId() {
        UserId userId = UserId.generate();

        when(jpaRepository.countByUserId(userId.value())).thenReturn(5L);

        long result = orderRepositoryAdapter.countByUserId(userId);

        assertThat(result).isEqualTo(5L);

        verify(jpaRepository).countByUserId(userId.value());
    }

    @Test
    @DisplayName("Should find top users by total spent")
    void shouldFindTopUsersByTotalSpent() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        int limit = 10;

        Object[] result1 = {UserId.generate().value(), "user1@example.com", new BigDecimal("1000.00"), 5L};
        Object[] result2 = {UserId.generate().value(), "user2@example.com", new BigDecimal("750.00"), 3L};
        List<Object[]> queryResults = Arrays.asList(result1, result2);

        when(jpaRepository.findTopUsersByTotalSpent(startDate, endDate, limit)).thenReturn(queryResults);

        List<TopUserReport> result = orderRepositoryAdapter.findTopUsersByTotalSpent(startDate, endDate, limit);

        assertThat(result).hasSize(2).first().satisfies(topUser -> {
            assertThat(topUser.userName()).isEqualTo("user1@example.com");
            assertThat(topUser.totalSpent()).isEqualTo(new Money(new BigDecimal("1000.00")));
            assertThat(topUser.orderCount()).isEqualTo(5);
        });

        verify(jpaRepository).findTopUsersByTotalSpent(startDate, endDate, limit);
    }

    @Test
    @DisplayName("Should find user ticket average")
    void shouldFindUserTicketAverage() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        Object[] result1 = {UserId.generate().value(), "user1@example.com", new BigDecimal("200.00")};
        Object[] result2 = {UserId.generate().value(), "user2@example.com", new BigDecimal("150.00")};
        List<Object[]> queryResults = Arrays.asList(result1, result2);

        when(jpaRepository.findUserTicketAverage(startDate, endDate)).thenReturn(queryResults);

        List<UserTicketReport> result = orderRepositoryAdapter.findUserTicketAverage(startDate, endDate);

        assertThat(result).hasSize(2).first().satisfies(report -> {
            assertThat(report.userName()).isEqualTo("user1@example.com");
            assertThat(report.averageTicketValue()).isEqualTo(new BigDecimal("200.00"));
        });

        verify(jpaRepository).findUserTicketAverage(startDate, endDate);
    }

    @Test
    @DisplayName("Should calculate monthly revenue")
    void shouldCalculateMonthlyRevenue() {
        LocalDateTime startDate =
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now()
                .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
        BigDecimal expectedRevenue = new BigDecimal("5000.00");

        when(jpaRepository.calculateMonthlyRevenue(startDate, endDate)).thenReturn(expectedRevenue);

        BigDecimal result = orderRepositoryAdapter.calculateMonthlyRevenue(startDate, endDate);

        assertThat(result).isEqualTo(expectedRevenue);

        verify(jpaRepository).calculateMonthlyRevenue(startDate, endDate);
    }

    @Test
    @DisplayName("Should return zero when monthly revenue is null")
    void shouldReturnZero_WhenMonthlyRevenueIsNull() {
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime endDate = LocalDateTime.now();

        when(jpaRepository.calculateMonthlyRevenue(startDate, endDate)).thenReturn(null);

        BigDecimal result = orderRepositoryAdapter.calculateMonthlyRevenue(startDate, endDate);

        assertThat(result).isZero();

        verify(jpaRepository).calculateMonthlyRevenue(startDate, endDate);
    }

    @Test
    @DisplayName("Should handle empty results in findByUserId")
    void shouldHandleEmptyResults_InFindByUserId() {
        UserId userId = UserId.generate();

        when(jpaRepository.findByUserId(userId.value())).thenReturn(Collections.emptyList());

        List<OrderDomain> result = orderRepositoryAdapter.findByUserId(userId);

        assertThat(result).isEmpty();

        verify(jpaRepository).findByUserId(userId.value());
    }

    @Test
    @DisplayName("Should properly delegate to mapper for entity conversion")
    void shouldProperlyDelegate_ToMapperForEntityConversion() {
        OrderDomain orderDomain = createOrderDomain(OrderDomain.OrderStatus.PENDING);
        OrderJpaEntity orderEntity = createOrderEntity();

        when(mapper.toEntity(orderDomain)).thenReturn(orderEntity);
        when(jpaRepository.save(any(OrderJpaEntity.class))).thenReturn(orderEntity);
        when(mapper.toDomain(orderEntity)).thenReturn(orderDomain);

        orderRepositoryAdapter.save(orderDomain);

        verify(mapper).toEntity(orderDomain);
        verify(mapper).toDomain(orderEntity);
    }

    @Test
    @DisplayName("Should create pageable with unsorted when no sort is provided")
    void shouldCreatePageable_WithUnsortedWhenNoSortProvided() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(createOrderDomain(OrderDomain.OrderStatus.PENDING));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create pageable with ascending sort when direction not specified")
    void shouldCreatePageable_WithAscendingSortWhenDirectionNotSpecified() {
        PageRequest pageRequest = PageRequest.of(0, 10, "name");
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(createOrderDomain(OrderDomain.OrderStatus.PENDING));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return false when orders do not exist by product id")
    void shouldReturnFalse_WhenOrdersDoNotExistByProductId() {
        ProductId productId = ProductId.generate();

        when(jpaRepository.existsByProductId(productId.value())).thenReturn(false);

        boolean result = orderRepositoryAdapter.existsByProductId(productId);

        assertThat(result).isFalse();

        verify(jpaRepository).existsByProductId(productId.value());
    }

    @Test
    @DisplayName("Should handle empty results for paginated user orders")
    void shouldHandleEmptyResults_ForPaginatedUserOrders() {
        UserId userId = UserId.generate();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<OrderJpaEntity> emptyPage =
                new PageImpl<>(Collections.emptyList(), org.springframework.data.domain.PageRequest.of(0, 10), 0);

        when(jpaRepository.findByUserIdOrderByCreatedAtDesc(eq(userId.value()), any(Pageable.class)))
                .thenReturn(emptyPage);

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findByUserIdPaginated(userId, pageRequest);

        assertThat(result).isNotNull().satisfies(response -> {
            assertThat(response.content()).isEmpty();
            assertThat(response.elements()).isZero();
        });

        verify(jpaRepository).findByUserIdOrderByCreatedAtDesc(eq(userId.value()), any(Pageable.class));
    }

    @Test
    @DisplayName("Should create pageable with descending sort when only desc direction specified")
    void shouldCreatePageable_WithDescendingSortWhenOnlyDescDirectionSpecified() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt,DESC");
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(createOrderDomain(OrderDomain.OrderStatus.PENDING));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create pageable with ascending sort when only property name provided")
    void shouldCreatePageable_WithAscendingSortWhenOnlyPropertyNameProvided() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt");
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(createOrderDomain(OrderDomain.OrderStatus.PENDING));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create pageable with ascending sort when non-desc direction specified")
    void shouldCreatePageable_WithAscendingSortWhenNonDescDirectionSpecified() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt,asc");
        List<OrderJpaEntity> entities = Collections.singletonList(createOrderEntity());
        Page<OrderJpaEntity> page = new PageImpl<>(entities, org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(entities.getFirst())).thenReturn(createOrderDomain(OrderDomain.OrderStatus.PENDING));

        PageResponse<OrderDomain> result = orderRepositoryAdapter.findAllPaginated(pageRequest);

        assertThat(result).isNotNull().satisfies(response -> assertThat(response.content())
                .hasSize(1));

        verify(jpaRepository).findAll(any(Pageable.class));
    }

    private OrderDomain createOrderDomain(OrderDomain.OrderStatus status) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("99.99")), 1);
        return new OrderDomain(
                OrderId.generate(),
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("99.99")),
                status,
                LocalDateTime.now());
    }

    private OrderJpaEntity createOrderEntity() {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(OrderId.generate().value());
        entity.setUserId(UserId.generate().value());
        entity.setTotalAmount(new BigDecimal("99.99"));
        entity.setStatus(OrderJpaEntity.OrderStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
