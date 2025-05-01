package mbm.brokerage_backend.asset.web;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.domain.SetBalanceDto;
import mbm.brokerage_backend.asset.web.mapper.AssetDtoToResponseMapper;
import mbm.brokerage_backend.asset.web.mapper.SetBalanceRequestToDtoMapper;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import mbm.brokerage_backend.asset.web.model.SetBalanceRequest;
import mbm.brokerage_backend.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AssetControllerUT {

    private static final int SIZE = create(Integer.class);

    @Mock private AssetService assetService;
    @Mock private AssetDtoToResponseMapper assetDtoToResponseMapper;
    @Mock private SetBalanceRequestToDtoMapper setBalanceRequestToDtoMapper;
    @Mock private AuthService authService;

    private AssetController assetController;

    @BeforeEach
    void setUp() {
        assetController = new AssetController(assetService, assetDtoToResponseMapper, setBalanceRequestToDtoMapper, authService);
    }

    @Test
    void test_listAssets() {
        // GIVEN
        final String customerId = create(String.class);
        final List<AssetDto> assets = ofList(AssetDto.class).size(SIZE).create();
        final List<AssetResponse> assetResponses = ofList(AssetResponse.class).size(SIZE).create();

        when(authService.canAccessCustomerData(customerId)).thenReturn(true);
        when(assetService.getAssets(customerId)).thenReturn(assets);
        when(assetDtoToResponseMapper.map(assets)).thenReturn(assetResponses);

        // WHEN
        final ResponseEntity<List<AssetResponse>> response = assetController.listAssets(customerId);

        // THEN
        assertThat(response).isNotNull().satisfies(assetResponse -> {
            assertThat(assetResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(assetResponse.getBody()).isNotNull().isEqualTo(assetResponses);
        });
        verify(authService).canAccessCustomerData(customerId);
        verify(assetService).getAssets(customerId);
        verify(assetDtoToResponseMapper).map(assets);
    }

    @Test
    void test_listAssets_Unauthorized() {
        // GIVEN
        final String customerId = create(String.class);

        when(authService.canAccessCustomerData(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThatThrownBy(() -> assetController.listAssets(customerId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN \"Access denied\"");

        verifyNoInteractions(assetService);
        verifyNoInteractions(assetDtoToResponseMapper);
    }

    @Test
    void test_setBalance() {
        // GIVEN
        final SetBalanceRequest setBalanceRequest = create(SetBalanceRequest.class);
        final SetBalanceDto setBalanceDto = create(SetBalanceDto.class);
        final AssetDto assetDto = create(AssetDto.class);
        final AssetResponse assetResponse = create(AssetResponse.class);

        when(setBalanceRequestToDtoMapper.map(setBalanceRequest)).thenReturn(setBalanceDto);
        when(assetService.setBalance(setBalanceDto)).thenReturn(assetDto);
        when(assetDtoToResponseMapper.map(assetDto)).thenReturn(assetResponse);

        // WHEN
        final ResponseEntity<AssetResponse> response = assetController.setBalance(setBalanceRequest);

        // THEN
        assertThat(response).isNotNull().satisfies(assetResp -> {
            assertThat(assetResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(assetResp.getBody()).isNotNull().isEqualTo(assetResponse);
        });
        verify(setBalanceRequestToDtoMapper).map(setBalanceRequest);
        verify(assetService).setBalance(setBalanceDto);
        verify(assetDtoToResponseMapper).map(assetDto);
    }
}
