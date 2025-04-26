package mbm.brokerage_backend.asset.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.asset.repository.AssetRepository;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import mbm.brokerage_backend.asset.repository.mapper.AssetEntityToDtoMapper;
import org.springframework.stereotype.Service;

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
}
