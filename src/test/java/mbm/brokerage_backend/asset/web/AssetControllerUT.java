package mbm.brokerage_backend.asset.web;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.web.mapper.AssetDtoToResponseMapper;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AssetControllerUT {

    private static final int SIZE = create(Integer.class);

    @Mock private AssetService assetService;
    @Mock private AssetDtoToResponseMapper assetDtoToResponseMapper;

    private AssetController assetController;

    @BeforeEach
    void setUp() {
        assetController = new AssetController(assetService, assetDtoToResponseMapper);
    }

    @Test
    void test_listAssets() {
        // GIVEN
        final String customerId = create(String.class);
        final List<AssetDto> assets = ofList(AssetDto.class).size(SIZE).create();
        final List<AssetResponse> assetResponses = ofList(AssetResponse.class).size(SIZE).create();

        when(assetService.getAssets(customerId)).thenReturn(assets);
        when(assetDtoToResponseMapper.map(assets)).thenReturn(assetResponses);

        // WHEN
        final ResponseEntity<List<AssetResponse>> response = assetController.listAssets(customerId);

        // THEN
        assertThat(response).isNotNull().satisfies(assetResponse -> {
            assertThat(assetResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(assetResponse.getBody()).isNotNull().isEqualTo(assetResponses);
        });
    }
}