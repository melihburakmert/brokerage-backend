package mbm.brokerage_backend.asset.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.common.AssetNotFoundException;
import mbm.brokerage_backend.asset.repository.AssetRepository;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import mbm.brokerage_backend.asset.repository.mapper.AssetEntityToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetServiceImp implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetEntityToDtoMapper assetEntityToDtoMapper;

    public AssetServiceImp(
            final AssetRepository assetRepository,
            final AssetEntityToDtoMapper assetEntityToDtoMapper) {
        this.assetRepository = assetRepository;
        this.assetEntityToDtoMapper = assetEntityToDtoMapper;
    }

    @Override
    @Transactional
    public AssetDto initializeAsset(final String customerId, final String assetName) {
        final AssetEntity assetEntity = buildAsset(customerId, assetName);

        final AssetEntity savedAsset = assetRepository.save(assetEntity);
        return assetEntityToDtoMapper.map(savedAsset);
    }

    @Override
    public List<AssetDto> getAssets(final String customerId) {
        final List<AssetEntity> assetEntities = assetRepository.findByCustomerId(customerId);
        return assetEntityToDtoMapper.map(assetEntities);
    }

    @Override
    public AssetDto getAssetByCustomerIdAndAssetName(final String customerId, final String assetName) {
        final AssetEntity assetEntity = getAssetEntity(customerId, assetName);
        return assetEntityToDtoMapper.map(assetEntity);
    }

    @Override
    @Transactional
    public void updateAssetSize(final String customerId, final String assetName, final BigDecimal newSize) {
        final AssetEntity asset = getAssetEntity(customerId, assetName);

        asset.setSize(newSize);
        assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void updateAssetUsableSize(final String customerId, final String assetName, final BigDecimal newUsableSize) {
        final AssetEntity asset = getAssetEntity(customerId, assetName);

        asset.setUsableSize(newUsableSize);
        assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void updateAssetSizeAndUsableSize(final String customerId, final String assetName, final BigDecimal newSize, final BigDecimal newUsableSize) {
        final AssetEntity asset = getAssetEntity(customerId, assetName);

        asset.setSize(newSize);
        asset.setUsableSize(newUsableSize);
        assetRepository.save(asset);
    }

    @Override
    public boolean isAssetExists(final String customerId, final String assetName) {
        return assetRepository.existsByCustomerIdAndAssetName(customerId, assetName);
    }

    private AssetEntity getAssetEntity(final String customerId, final String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new AssetNotFoundException(customerId, assetName));
    }

    private AssetEntity buildAsset(final String customerId, final String assetName) {
        return AssetEntity.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(BigDecimal.ZERO)
                .usableSize(BigDecimal.ZERO)
                .build();
    }
}
