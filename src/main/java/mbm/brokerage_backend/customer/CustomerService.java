package mbm.brokerage_backend.customer;


import java.util.Optional;

public interface CustomerService {
    void registerCustomer(String username, String encodedPassword);

    boolean existsByUsername(String username);

    Optional<CustomerDto> findByUsername(String username);
}