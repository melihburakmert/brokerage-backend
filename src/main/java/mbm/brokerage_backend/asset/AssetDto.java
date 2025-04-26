package mbm.brokerage_backend.asset;

import java.math.BigDecimal;

public record AssetDto(
    String id,
    String customerId,
    String assetName,
    BigDecimal size,
    BigDecimal usableSize
) {
}