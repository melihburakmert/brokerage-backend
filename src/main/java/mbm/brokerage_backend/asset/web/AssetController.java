package mbm.brokerage_backend.asset.web;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.web.api.AssetsApiDelegate;
import mbm.brokerage_backend.asset.web.mapper.AssetDtoToResponseMapper;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import mbm.brokerage_backend.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class AssetController implements AssetsApiDelegate {

    private final AssetService assetService;
    private final AssetDtoToResponseMapper assetDtoToResponseMapper;
    private final AuthService authService;

    public AssetController(final AssetService assetService,
                           final AssetDtoToResponseMapper assetDtoToResponseMapper,
                           final AuthService authService) {
        this.assetService = assetService;
        this.assetDtoToResponseMapper = assetDtoToResponseMapper;
        this.authService = authService;
    }

    @Override
    public ResponseEntity<List<AssetResponse>> listAssets(final String customerId) {
        if (!authService.canAccessCustomerData(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        final List<AssetDto> assets = assetService.getAssets(customerId);
        final List<AssetResponse> assetResponses = assetDtoToResponseMapper.map(assets);

        return ResponseEntity.ok(assetResponses);
    }
}
