package mbm.brokerage_backend.asset.web;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.web.api.AssetsApiDelegate;
import mbm.brokerage_backend.asset.web.mapper.AssetDtoToResponseMapper;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetController implements AssetsApiDelegate {

    private final AssetService assetService;
    private final AssetDtoToResponseMapper assetDtoToResponseMapper;

    public AssetController(final AssetService assetService, final AssetDtoToResponseMapper assetDtoToResponseMapper) {
        this.assetService = assetService;
        this.assetDtoToResponseMapper = assetDtoToResponseMapper;
    }

    @Override
    public ResponseEntity<List<AssetResponse>> listAssets(final String customerId) {
        final List<AssetDto> assets = assetService.getAssets(customerId);
        final List<AssetResponse> assetResponses = assetDtoToResponseMapper.map(assets);

        return ResponseEntity.ok(assetResponses);
    }
}
