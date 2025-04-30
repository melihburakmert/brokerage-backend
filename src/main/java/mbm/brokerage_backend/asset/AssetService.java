package mbm.brokerage_backend.asset;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {

    List<AssetDto> getAssets(String customerId);

    AssetDto getAssetByCustomerIdAndAssetName(String customerId, String assetName);

    AssetDto initializeAsset(String customerId, String assetName);

    void updateAssetSize(String customerId, String assetName, BigDecimal newSize);

    void updateAssetUsableSize(String customerId, String assetName, BigDecimal newUsableSize);

    void updateAssetSizeAndUsableSize(String customerId, String assetName, BigDecimal newSize, BigDecimal newUsableSize);

    boolean isAssetExists(String customerId, String assetName);
}

