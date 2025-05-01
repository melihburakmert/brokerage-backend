package mbm.brokerage_backend.asset.web.mapper;

import mbm.brokerage_backend.asset.domain.SetBalanceDto;
import mbm.brokerage_backend.asset.web.model.SetBalanceRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SetBalanceRequestToDtoMapperUT {

    private SetBalanceRequestToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SetBalanceRequestToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final SetBalanceRequest request = Instancio.create(SetBalanceRequest.class);
        
        // WHEN
        final SetBalanceDto result = mapper.map(request);
        
        // THEN
        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(request.getCustomerId());
        assertThat(result.balance()).isEqualTo(request.getBalance());
    }
}
