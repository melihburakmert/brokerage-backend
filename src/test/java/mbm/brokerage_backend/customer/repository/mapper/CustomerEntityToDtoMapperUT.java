package mbm.brokerage_backend.customer.repository.mapper;

import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Might remove
class CustomerEntityToDtoMapperUT {

    private CustomerEntityToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerEntityToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final CustomerEntity customerEntity = Instancio.create(CustomerEntity.class);

        // WHEN
        final CustomerDto result = mapper.map(customerEntity);

        // THEN
        assertThat(result).isNotNull().satisfies(
                dto -> assertThat(dto.id()).isEqualTo(customerEntity.getId()),
                dto -> assertThat(dto.username()).isEqualTo(customerEntity.getUsername()),
                dto -> assertThat(dto.role()).isEqualTo(customerEntity.getRole())
        );
    }

}