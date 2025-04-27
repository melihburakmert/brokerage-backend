package mbm.brokerage_backend.asset.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.exception.AssetNotFoundException;
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
    public List<AssetDto> getAssets(final String customerId) {
        final List<AssetEntity> assetEntities = assetRepository.findByCustomerId(customerId);
        return assetEntityToDtoMapper.map(assetEntities);
    }

    @Override
    public AssetDto getAssetByCustomerIdAndAssetName(final String customerId, final String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(assetEntityToDtoMapper::map)
                .orElseThrow(() -> new AssetNotFoundException(customerId, assetName));
    }

    @Override
    @Transactional
    public AssetDto updateAssetUsableSize(final String customerId, final String assetName, final BigDecimal newUsableSize) {
        final AssetEntity asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new AssetNotFoundException(customerId, assetName));

        asset.setUsableSize(newUsableSize);

        final AssetEntity updatedAsset = assetRepository.save(asset);
        return assetEntityToDtoMapper.map(updatedAsset);
    }
}
