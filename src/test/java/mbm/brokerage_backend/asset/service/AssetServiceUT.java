package mbm.brokerage_backend.asset.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.exception.AssetNotFoundException;
import mbm.brokerage_backend.asset.repository.AssetRepository;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import mbm.brokerage_backend.asset.repository.mapper.AssetEntityToDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceUT {

    private static final int SIZE = create(Integer.class);

    @Mock private AssetRepository assetRepository;
    @Mock private AssetEntityToDtoMapper assetEntityToDtoMapper;

    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetServiceImp(assetRepository, assetEntityToDtoMapper);
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

        when(assetRepository.findByCustomerId(customerId)).thenReturn(assetEntities);
        when(assetEntityToDtoMapper.map(assetEntities)).thenReturn(assetDtos);

        // WHEN
        final List<AssetDto> result = assetService.getAssets(customerId);

        // THEN
        assertThat(result).isNotNull().hasSize(SIZE).containsExactlyElementsOf(assetDtos);
        verify(assetRepository).findByCustomerId(customerId);
        verify(assetEntityToDtoMapper).map(assetEntities);
    }

    @Test
    void test_getAssetByCustomerIdAndAssetName() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        final AssetEntity assetEntity = create(AssetEntity.class);
        final AssetDto assetDto = create(AssetDto.class);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));
        when(assetEntityToDtoMapper.map(assetEntity)).thenReturn(assetDto);

        // WHEN
        final AssetDto result = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);

        // THEN
        assertThat(result).isNotNull().isEqualTo(assetDto);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetEntityToDtoMapper).map(assetEntity);
    }

    @Test
    void test_getAssetByCustomerIdAndAssetName_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.getAssetByCustomerIdAndAssetName(customerId, assetName));
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

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetSize(customerId, assetName, newSize);

        // THEN
        assertThat(assetEntity.getSize()).isEqualTo(newSize);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetSize(customerId, assetName, newSize));
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

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);

        // THEN
        assertThat(assetEntity.getUsableSize()).isEqualTo(newUsableSize);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetUsableSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetUsableSize(customerId, assetName, newUsableSize));
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

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(assetEntity));

        // WHEN
        assetService.updateAssetSizeAndUsableSize(customerId, assetName, newSize, newUsableSize);

        // THEN
        assertThat(assetEntity.getSize()).isEqualTo(newSize);
        assertThat(assetEntity.getUsableSize()).isEqualTo(newUsableSize);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(assetRepository).save(assetEntity);
    }

    @Test
    void test_updateAssetSizeAndUsableSize_assetNotFound() {
        // GIVEN
        final String customerId = create(String.class);
        final String assetName = create(String.class);
        final BigDecimal newSize = create(BigDecimal.class);
        final BigDecimal newUsableSize = create(BigDecimal.class);

        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(AssetNotFoundException.class, () -> assetService.updateAssetSizeAndUsableSize(customerId, assetName, newSize, newUsableSize));
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoMoreInteractions(assetRepository);
    }
}