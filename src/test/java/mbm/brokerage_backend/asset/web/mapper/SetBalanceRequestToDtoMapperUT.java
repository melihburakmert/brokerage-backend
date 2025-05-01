package mbm.brokerage_backend.asset.web.mapper;

import mbm.brokerage_backend.asset.domain.SetBalanceDto;
import mbm.brokerage_backend.asset.web.model.SetBalanceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;

class SetBalanceRequestToDtoMapperUT {

    private SetBalanceRequestToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SetBalanceRequestToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final SetBalanceRequest request = create(SetBalanceRequest.class);
        
        // WHEN
        final SetBalanceDto result = mapper.map(request);
        
        // THEN
        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(request.getCustomerId());
        assertThat(result.balance()).isEqualTo(request.getBalance());
    }
}
