package mbm.brokerage_backend.customer.repository.mapper;

import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityToDtoMapper {

    public CustomerDto map(final CustomerEntity customerEntity) {
        return CustomerDto.builder()
                .username(customerEntity.getUsername())
                .password(customerEntity.getPassword())
                .role(customerEntity.getRole())
                .build();
    }
}
