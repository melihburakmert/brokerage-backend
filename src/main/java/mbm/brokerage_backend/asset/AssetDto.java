package mbm.brokerage_backend.asset;

import java.math.BigDecimal;
import java.time.Instant;

public record AssetDto(
    String id,
    String customerId,
    String assetName,
    BigDecimal size,
    BigDecimal usableSize,
    Instant lastUpdated
) {
}