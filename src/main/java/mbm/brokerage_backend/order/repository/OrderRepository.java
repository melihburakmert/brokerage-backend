package mbm.brokerage_backend.order.repository;

import mbm.brokerage_backend.order.domain.OrderStatus;
import mbm.brokerage_backend.order.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerIdAndCreateDateBetween(String customerId, Instant fromDate, Instant toDate);

    Optional<OrderEntity> findByIdAndStatus(Long id, OrderStatus status);
}
