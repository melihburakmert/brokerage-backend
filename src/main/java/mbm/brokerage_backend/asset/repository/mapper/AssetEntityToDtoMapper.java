package mbm.brokerage_backend.asset.repository.mapper;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetEntityToDtoMapper {

    public List<AssetDto> map(final List<AssetEntity> assetEntities) {
        return assetEntities.stream()
            .map(this::map)
            .toList();
    }

    public AssetDto map(final AssetEntity assetEntity) {
        return new AssetDto(
            assetEntity.getId(),
            assetEntity.getCustomerId(),
            assetEntity.getAssetName(),
            assetEntity.getSize(),
            assetEntity.getUsableSize(),
            assetEntity.getLastUpdated()
        );
    }
}
