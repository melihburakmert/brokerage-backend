package mbm.brokerage_backend.asset.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SetBalanceDto(String customerId, BigDecimal balance) {
}
