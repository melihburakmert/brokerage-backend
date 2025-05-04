package mbm.brokerage_backend.customer.service;

import lombok.RequiredArgsConstructor;
import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.customer.Role;
import mbm.brokerage_backend.customer.repository.CustomerRepository;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import mbm.brokerage_backend.customer.repository.mapper.CustomerEntityToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerEntityToDtoMapper customerEntityToDtoMapper;

    @Override
    @Transactional
    public void registerCustomer(final String username, final String encodedPassword) {
        final CustomerEntity customer = createCustomer(username, encodedPassword);
        customerRepository.save(customer);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return customerRepository.findByUsername(username).isPresent();
    }

    @Override
    public Optional<CustomerDto> findByUsername(final String username) {
        return customerRepository.findByUsername(username).map(customerEntityToDtoMapper::map);
    }

    private CustomerEntity createCustomer(final String username, final String encodedPassword) {
        return CustomerEntity.builder()
                .username(username)
                .password(encodedPassword)
                .role(Role.CUSTOMER)
                .build();
    }
}