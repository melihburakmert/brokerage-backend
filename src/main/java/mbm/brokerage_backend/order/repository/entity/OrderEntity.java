package mbm.brokerage_backend.order.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbm.brokerage_backend.order.domain.OrderSide;
import mbm.brokerage_backend.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    @Column(name = "asset_name", nullable = false, length = 5)
    private String assetName;

    @Column(name = "order_side", nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    @Column(name = "size", nullable = false, precision = 19, scale = 8)
    private BigDecimal size;

    @Column(name = "price", nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "create_date", nullable = false)
    private Instant createDate;
}