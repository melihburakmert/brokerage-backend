package mbm.brokerage_backend.order;

import lombok.Builder;
import mbm.brokerage_backend.order.domain.OrderSide;
import mbm.brokerage_backend.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record OrderDto(
    Long id,
    String customerId,
    String assetName,
    OrderSide orderSide,
    BigDecimal size,
    BigDecimal price,
    OrderStatus status,
    Instant createDate
) {
}