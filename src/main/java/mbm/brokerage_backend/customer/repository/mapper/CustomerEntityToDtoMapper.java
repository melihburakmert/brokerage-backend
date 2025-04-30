package mbm.brokerage_backend.customer.repository.mapper;

import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.springframework.stereotype.Component;

// TODO: Might be removed
@Component
public class CustomerEntityToDtoMapper {

    public CustomerDto map(final CustomerEntity entity) {
        return new CustomerDto(
                entity.getId(),
                entity.getUsername(),
                entity.getRole()
        );
    }
}
