package mbm.brokerage_backend.asset.web.mapper;

import mbm.brokerage_backend.asset.domain.SetBalanceDto;
import mbm.brokerage_backend.asset.web.model.SetBalanceRequest;
import org.springframework.stereotype.Component;

@Component
public class SetBalanceRequestToDtoMapper {
    public SetBalanceDto map(final SetBalanceRequest request) {
        return SetBalanceDto.builder()
                .customerId(request.getCustomerId())
                .balance(request.getBalance())
                .build();
    }
}
