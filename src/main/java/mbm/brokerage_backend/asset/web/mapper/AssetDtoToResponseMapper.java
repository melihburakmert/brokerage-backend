package mbm.brokerage_backend.asset.web.mapper;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetDtoToResponseMapper {

    public List<AssetResponse> map(final List<AssetDto> assetDtos) {
        return assetDtos.stream()
            .map(this::map)
            .toList();
    }

    public AssetResponse map(final AssetDto assetDto) {
        return new AssetResponse()
            .id(assetDto.id())
            .customerId(assetDto.customerId())
            .assetName(assetDto.assetName())
            .size(assetDto.size())
            .usableSize(assetDto.usableSize()
        );
    }
}
