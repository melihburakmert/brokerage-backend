package mbm.brokerage_backend.customer.service;

import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.customer.Role;
import mbm.brokerage_backend.customer.repository.CustomerRepository;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUT {

    @Mock private CustomerRepository customerRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImp(customerRepository, passwordEncoder);
    }

    @Test
    void test_registerCustomer() {
        // GIVEN
        final String username = create(String.class);
        final String password = create(String.class);
        final String encodedPassword = create(String.class);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // WHEN
        customerService.registerCustomer(username, password);

        // THEN
        final ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
        customerRepository.save(captor.capture());
        final CustomerEntity customer = captor.getValue();
        assertThat(customer.getUsername()).isEqualTo(username);
        assertThat(customer.getPassword()).isEqualTo(encodedPassword);
        assertThat(customer.getRole()).isEqualTo(Role.CUSTOMER);
        verify(passwordEncoder).encode(encodedPassword);
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

}