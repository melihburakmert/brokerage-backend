package mbm.brokerage_backend.customer.service;

import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.customer.Role;
import mbm.brokerage_backend.customer.repository.CustomerRepository;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import mbm.brokerage_backend.customer.repository.mapper.CustomerEntityToDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUT {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerEntityToDtoMapper customerEntityToDtoMapper;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImp(customerRepository, customerEntityToDtoMapper);
    }

    @Test
    void test_registerCustomer() {
        // GIVEN
        final String username = create(String.class);
        final String password = create(String.class);


        // WHEN
        customerService.registerCustomer(username, password);

        // THEN
        final ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).save(captor.capture());
        final CustomerEntity customer = captor.getValue();
        assertThat(customer.getUsername()).isEqualTo(username);
        assertThat(customer.getPassword()).isEqualTo(password);
        assertThat(customer.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void test_existsByUsername() {
        // GIVEN
        final String username = create(String.class);
        final CustomerEntity customer = create(CustomerEntity.class);

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));

        // WHEN
        final boolean exists = customerService.existsByUsername(username);

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void test_doesNotExistByUsername() {
        // GIVEN
        final String username = create(String.class);

        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());

        // WHEN
        final boolean exists = customerService.existsByUsername(username);

        // THEN
        assertThat(exists).isFalse();
    }

    @Test
    void test_findByUsername() {
        // GIVEN
        final String username = create(String.class);
        final CustomerEntity customer = create(CustomerEntity.class);
        final CustomerDto customerDto = create(CustomerDto.class);

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));
        when(customerEntityToDtoMapper.map(customer)).thenReturn(customerDto);

        // WHEN
        final Optional<CustomerDto> result = customerService.findByUsername(username);

        // THEN
        assertThat(result).isPresent().hasValue(customerDto);
    }

}