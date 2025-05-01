package mbm.brokerage_backend.asset.repository.mapper;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;

class AssetEntityToDtoMapperUT {

    private static final int SIZE = create(Integer.class);

    private AssetEntityToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AssetEntityToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final AssetEntity assetEntity = create(AssetEntity.class);

        // WHEN
        final AssetDto result = mapper.map(assetEntity);

        // THEN
        assertThat(result).isNotNull().satisfies(assetDto -> {
            assertThat(assetDto.id()).isEqualTo(assetEntity.getId());
            assertThat(assetDto.customerId()).isEqualTo(assetEntity.getCustomerId());
            assertThat(assetDto.assetName()).isEqualTo(assetEntity.getAssetName());
            assertThat(assetDto.size()).isEqualTo(assetEntity.getSize());
            assertThat(assetDto.usableSize()).isEqualTo(assetEntity.getUsableSize());
        });
    }

    @Test
    void test_mapList() {
        // GIVEN
        final List<AssetEntity> assetEntities = ofList(AssetEntity.class).size(SIZE).create();

        // WHEN
        final List<AssetDto> result = mapper.map(assetEntities);

        // THEN
        assertThat(result).isNotNull().hasSize(SIZE);
    }

}