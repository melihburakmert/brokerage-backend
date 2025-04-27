package mbm.brokerage_backend.order.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateOrderDto(
    @NotNull
    String customerId,
    
    @NotNull
    @Size(max = 5)
    String assetName,
    
    @NotNull
    OrderSide orderSide,
    
    @NotNull
    BigDecimal size,
    
    @NotNull
    BigDecimal price
) {
}