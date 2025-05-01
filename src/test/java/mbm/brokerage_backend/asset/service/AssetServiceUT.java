package mbm.brokerage_backend.asset.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.domain.SetBalanceDto;
import mbm.brokerage_backend.common.AssetNotFoundException;
import mbm.brokerage_backend.common.CustomerNotFoundException;
import mbm.brokerage_backend.asset.repository.AssetRepository;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import mbm.brokerage_backend.asset.repository.mapper.AssetEntityToDtoMapper;
import mbm.brokerage_backend.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceUT {

    private static final int SIZE = create(Integer.class);
    private static final String TRY_ASSET = "TRY";

    @Mock private AssetRepository assetRepository;
    @Mock private AssetEntityToDtoMapper assetEntityToDtoMapper;
    @Mock private CustomerService customerService;

    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetServiceImp(assetRepository, assetEntityToDtoMapper, customerService);
    }

    @Test
    void test_getAssets() {
        // GIVEN
        final String customerId = create(String.class);
        final List<AssetEntity> assetEntities = ofList(AssetEntity.class)
                .size(SIZE)
                .create();

        final List<AssetDto> assetDtos = ofList(AssetDto.class)
                .size(SIZE)
                .create();

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerId(customerId)).thenReturn(assetEntities);
        when(assetEntityToDtoMapper.map(assetEntities)).thenReturn(assetDtos);

        // WHEN
        final List<AssetDto> result = assetService.getAssets(customerId);

        // THEN
        assertThat(result).isNotNull().hasSize(SIZE).containsExactlyElementsOf(assetDtos);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerId(customerId);
        verify(assetEntityToDtoMapper).map(assetEntities);
    }

    @Test
    void test_getAssets_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.getAssets(customerId));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(assetEntityToDtoMapper);
    }

    @Test
    void test_getAssetByCustomerIdAndAssetName() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        final AssetEntity assetEntity = create(AssetEntity.class);
        final AssetDto assetDto = create(AssetDto.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));
        when(assetEntityToDtoMapper.map(assetEntity)).thenReturn(assetDto);

        // WHEN
        final AssetDto result = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);

        // THEN
        assertThat(result).isNotNull().isEqualTo(assetDto);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetEntityToDtoMapper).map(assetEntity);
    }

    @Test
    void test_getAssetByCustomerIdAndAssetName_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.getAssetByCustomerIdAndAssetName(customerId, assetName));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(assetEntityToDtoMapper);
    }

    @Test
    void test_getAssetByCustomerIdAndAssetName_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.getAssetByCustomerIdAndAssetName(customerId, assetName));
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoInteractions(assetEntityToDtoMapper);
    }

    @Test
    void test_updateAssetSize() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);

        final AssetEntity assetEntity = create(AssetEntity.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetSize(customerId, assetName, newSize);

        // THEN
        assertThat(assetEntity.getSize()).isEqualTo(newSize);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetSize_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.updateAssetSize(customerId, assetName, newSize));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
    }

    @Test
    void test_updateAssetSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetSize(customerId, assetName, newSize));
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoMoreInteractions(assetRepository);
    }

    @Test
    void test_updateAssetUsableSize() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        final AssetEntity assetEntity = create(AssetEntity.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);

        // THEN
        assertThat(assetEntity.getUsableSize()).isEqualTo(newUsableSize);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetUsableSize_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.updateAssetUsableSize(customerId, assetName, newUsableSize));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
    }

    @Test
    void test_updateAssetUsableSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetUsableSize(customerId, assetName, newUsableSize));
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoMoreInteractions(assetRepository);
    }

    @Test
    void test_updateAssetSizeAndUsableSize() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        final AssetEntity assetEntity = create(AssetEntity.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetSizeAndUsableSize(customerId, assetName, newSize, newUsableSize);

        // THEN
        assertThat(assetEntity.getSize()).isEqualTo(newSize);
        assertThat(assetEntity.getUsableSize()).isEqualTo(newUsableSize);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetSizeAndUsableSize_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.updateAssetSizeAndUsableSize(customerId, assetName, newSize, newUsableSize));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
    }

    @Test
    void test_updateAssetSizeAndUsableSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetSizeAndUsableSize(customerId, assetName, newSize, newUsableSize));
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoMoreInteractions(assetRepository);
    }

    @Test
    void test_initializeAsset() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        final AssetEntity expectedEntity = AssetEntity.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(BigDecimal.ZERO)
                .usableSize(BigDecimal.ZERO)
                .build();

        final AssetEntity savedEntity = create(AssetEntity.class);
        final AssetDto expectedDto = create(AssetDto.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.save(expectedEntity)).thenReturn(savedEntity);
        when(assetEntityToDtoMapper.map(savedEntity)).thenReturn(expectedDto);

        // WHEN
        final AssetDto result = assetService.initializeAsset(customerId, assetName);

        // THEN
        assertThat(result).isNotNull().isEqualTo(expectedDto);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).save(expectedEntity);
        verify(assetEntityToDtoMapper).map(savedEntity);
    }

    @Test
    void test_initializeAsset_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.initializeAsset(customerId, assetName));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(assetEntityToDtoMapper);
    }

    @Test
    void test_isAssetExists_whenAssetExists() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.existsByCustomerIdAndAssetName(customerId, assetName)).thenReturn(true);

        // WHEN
        final boolean result = assetService.isAssetExists(customerId, assetName);

        // THEN
        assertThat(result).isTrue();
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).existsByCustomerIdAndAssetName(customerId, assetName);
    }

    @Test
    void test_isAssetExists_customerNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.isAssetExists(customerId, assetName));
        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
    }

    @Test
    void test_isAssetExists_whenAssetDoesNotExist() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.existsByCustomerIdAndAssetName(customerId, assetName)).thenReturn(false);

        // WHEN
        final boolean result = assetService.isAssetExists(customerId, assetName);

        // THEN
        assertThat(result).isFalse();
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).existsByCustomerIdAndAssetName(customerId, assetName);
    }

    @Test
    void test_setBalance_whenCustomerDoesNotExist() {
        // GIVEN
        final String customerId = create(String.class);
        final BigDecimal balance = create(BigDecimal.class);
        final SetBalanceDto setBalanceDto = SetBalanceDto.builder()
                .customerId(customerId)
                .balance(balance)
                .build();

        when(customerService.existsByUsername(customerId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(CustomerNotFoundException.class, () -> assetService.setBalance(setBalanceDto));

        verify(customerService).existsByUsername(customerId);
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(assetEntityToDtoMapper);
    }

    @Test
    void test_setBalance_whenAssetExists() {
        // GIVEN
        final String customerId = create(String.class);
        final BigDecimal balance = create(BigDecimal.class);
        final SetBalanceDto setBalanceDto = SetBalanceDto.builder()
                .customerId(customerId)
                .balance(balance)
                .build();

        final AssetEntity existingAsset = create(AssetEntity.class);
        final AssetEntity savedAsset = create(AssetEntity.class);
        final AssetDto expectedDto = create(AssetDto.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.existsByCustomerIdAndAssetName(customerId, TRY_ASSET)).thenReturn(true);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, TRY_ASSET)).thenReturn(Optional.of(existingAsset));
        when(assetRepository.save(existingAsset)).thenReturn(savedAsset);
        when(assetEntityToDtoMapper.map(savedAsset)).thenReturn(expectedDto);

        // WHEN
        final AssetDto result = assetService.setBalance(setBalanceDto);

        // THEN
        assertThat(result).isNotNull().isEqualTo(expectedDto);
        assertThat(existingAsset.getSize()).isEqualTo(balance);
        assertThat(existingAsset.getUsableSize()).isEqualTo(balance);

        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).existsByCustomerIdAndAssetName(customerId, TRY_ASSET);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, TRY_ASSET);
        verify(assetRepository).save(existingAsset);
        verify(assetEntityToDtoMapper).map(savedAsset);
    }

    @Test
    void test_setBalance_whenAssetDoesNotExist() {
        // GIVEN
        final String customerId = create(String.class);
        final BigDecimal balance = create(BigDecimal.class);
        final SetBalanceDto setBalanceDto = SetBalanceDto.builder()
                .customerId(customerId)
                .balance(balance)
                .build();

        final AssetEntity savedAsset = create(AssetEntity.class);
        final AssetDto expectedDto = create(AssetDto.class);

        when(customerService.existsByUsername(customerId)).thenReturn(true);
        when(assetRepository.existsByCustomerIdAndAssetName(customerId, TRY_ASSET)).thenReturn(false);
        when(assetRepository.save(any(AssetEntity.class))).thenReturn(savedAsset);
        when(assetEntityToDtoMapper.map(savedAsset)).thenReturn(expectedDto);

        // WHEN
        final AssetDto result = assetService.setBalance(setBalanceDto);

        // THEN
        assertThat(result).isNotNull().isEqualTo(expectedDto);

        final ArgumentCaptor<AssetEntity> entityCaptor = ArgumentCaptor.forClass(AssetEntity.class);
        verify(customerService).existsByUsername(customerId);
        verify(assetRepository).existsByCustomerIdAndAssetName(customerId, TRY_ASSET);
        verify(assetRepository).save(entityCaptor.capture());
        verify(assetEntityToDtoMapper).map(savedAsset);

        final AssetEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getCustomerId()).isEqualTo(customerId);
        assertThat(capturedEntity.getAssetName()).isEqualTo(TRY_ASSET);
        assertThat(capturedEntity.getSize()).isEqualTo(balance);
        assertThat(capturedEntity.getUsableSize()).isEqualTo(balance);
    }
}
