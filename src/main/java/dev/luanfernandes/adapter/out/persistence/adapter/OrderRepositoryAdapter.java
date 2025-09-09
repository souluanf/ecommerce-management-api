package dev.luanfernandes.adapter.out.persistence.adapter;

import dev.luanfernandes.adapter.out.persistence.mapper.OrderEntityMapper;
import dev.luanfernandes.adapter.out.persistence.repository.OrderJpaRepository;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public OrderDomain save(OrderDomain order) {
        var existingEntity = jpaRepository.findById(order.getId().value()).orElse(null);
        var entity = mapper.toEntity(order);

        if (existingEntity != null) {
            existingEntity.setStatus(entity.getStatus());
            existingEntity.setTotalAmount(entity.getTotalAmount());
            existingEntity.setUpdatedAt(java.time.LocalDateTime.now());
            var savedEntity = jpaRepository.save(existingEntity);
            return mapper.toDomain(savedEntity);
        } else {
            var savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        }
    }

    @Override
    public Optional<OrderDomain> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<OrderDomain> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrderDomain> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return jpaRepository.findByDateRange(startDateTime, endDateTime).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrderDomain> findPaidOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return jpaRepository.findPaidOrdersByDateRange(startDateTime, endDateTime).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProductId(ProductId productId) {
        return jpaRepository.existsByProductId(productId.value());
    }

    @Override
    public List<OrderDomain> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResponse<OrderDomain> findAllPaginated(PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);

        var page = jpaRepository.findAll(pageable);

        var content = page.getContent().stream().map(mapper::toDomain).toList();

        return PageResponse.of(pageRequest.pageNumber(), pageRequest.pageSize(), page.getTotalElements(), content);
    }

    @Override
    public PageResponse<OrderDomain> findByUserIdPaginated(UserId userId, PageRequest pageRequest) {
        Pageable pageable = createPageable(pageRequest);

        var page = jpaRepository.findByUserIdOrderByCreatedAtDesc(userId.value(), pageable);

        var content = page.getContent().stream().map(mapper::toDomain).toList();

        return PageResponse.of(pageRequest.pageNumber(), pageRequest.pageSize(), page.getTotalElements(), content);
    }

    @Override
    public long countAll() {
        return jpaRepository.count();
    }

    @Override
    public long countByUserId(UserId userId) {
        return jpaRepository.countByUserId(userId.value());
    }

    @Override
    public List<TopUserReport> findTopUsersByTotalSpent(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return jpaRepository.findTopUsersByTotalSpent(startDate, endDate, limit).stream()
                .map(result -> new TopUserReport(
                        new UserId((String) result[0]),
                        (String) result[1],
                        Money.of((BigDecimal) result[2]),
                        ((Long) result[3]).intValue()))
                .toList();
    }

    @Override
    public List<UserTicketReport> findUserTicketAverage(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findUserTicketAverage(startDate, endDate).stream()
                .map(result -> new UserTicketReport(
                        new UserId((String) result[0]), (String) result[1], (BigDecimal) result[2]))
                .toList();
    }

    @Override
    public BigDecimal calculateMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = jpaRepository.calculateMonthlyRevenue(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private Pageable createPageable(PageRequest pageRequest) {
        Sort sort;

        if (pageRequest.hasSort()) {
            String[] sortParts = pageRequest.sort().split(",");
            String property = sortParts[0];
            Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, property);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return org.springframework.data.domain.PageRequest.of(pageRequest.pageNumber(), pageRequest.pageSize(), sort);
    }
}
