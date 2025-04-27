package mbm.brokerage_backend.asset;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {

    List<AssetDto> getAssets(String customerId);

    AssetDto getAssetByCustomerIdAndAssetName(String customerId, String assetName);

    AssetDto updateAssetUsableSize(String customerId, String assetName, BigDecimal newUsableSize);
}
