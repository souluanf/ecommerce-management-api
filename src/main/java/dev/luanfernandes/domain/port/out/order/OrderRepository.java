package dev.luanfernandes.domain.port.out.order;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    OrderDomain save(OrderDomain order);

    Optional<OrderDomain> findById(OrderId id);

    List<OrderDomain> findByUserId(UserId userId);

    List<OrderDomain> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<OrderDomain> findPaidOrdersByDateRange(LocalDate startDate, LocalDate endDate);

    boolean existsByProductId(ProductId productId);

    List<OrderDomain> findAll();

    PageResponse<OrderDomain> findAllPaginated(PageRequest pageRequest);

    PageResponse<OrderDomain> findByUserIdPaginated(UserId userId, PageRequest pageRequest);

    long countAll();

    long countByUserId(UserId userId);

    List<TopUserReport> findTopUsersByTotalSpent(LocalDateTime startDate, LocalDateTime endDate, int limit);

    List<UserTicketReport> findUserTicketAverage(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal calculateMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
