package mbm.brokerage_backend.customer.service;

import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.customer.Role;
import mbm.brokerage_backend.customer.repository.CustomerRepository;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImp(final CustomerRepository customerRepository, final PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerCustomer(final String username, final String password) {
        final CustomerEntity customer = createCustomer(username, password);
        customerRepository.save(customer);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return customerRepository.findByUsername(username).isPresent();
    }

    private CustomerEntity createCustomer(final String username, final String password) {
        return CustomerEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.CUSTOMER)
                .build();
    }
}