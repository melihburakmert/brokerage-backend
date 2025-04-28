package mbm.brokerage_backend.asset.web.mapper;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.web.model.AssetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;

class AssetDtoToResponseMapperUT {

    private static final int SIZE = create(Integer.class);

    private AssetDtoToResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AssetDtoToResponseMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final AssetDto assetDto = create(AssetDto.class);

        // WHEN
        final AssetResponse result = mapper.map(assetDto);

        // THEN
        assertThat(result).isNotNull().satisfies(assetResponse -> {
            assertThat(assetResponse.getId()).isEqualTo(assetDto.id());
            assertThat(assetResponse.getCustomerId()).isEqualTo(assetDto.customerId());
            assertThat(assetResponse.getAssetName()).isEqualTo(assetDto.assetName());
            assertThat(assetResponse.getSize()).isEqualTo(assetDto.size());
            assertThat(assetResponse.getUsableSize()).isEqualTo(assetDto.usableSize());
        });
    }

    @Test
    void test_mapList() {
        // GIVEN
        final List<AssetDto> assetDtos = ofList(AssetDto.class).size(SIZE).create();

        // WHEN
        final List<AssetResponse> result = mapper.map(assetDtos);

        // THEN
        assertThat(result).isNotNull().hasSize(SIZE);
    }
}