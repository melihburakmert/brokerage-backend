package mbm.brokerage_backend.customer;

public interface CustomerService {
    void registerCustomer(String username, String password);

    boolean existsByUsername(String username);
}