package mbm.brokerage_backend.asset;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetDto(
    String id,
    String customerId,
    String assetName,
    BigDecimal size,
    BigDecimal usableSize
) {
}