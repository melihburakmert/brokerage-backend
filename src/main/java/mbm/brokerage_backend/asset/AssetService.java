package mbm.brokerage_backend.asset;

import java.util.List;

public interface AssetService {

    List<AssetDto> getAssets(String customerId);
}
