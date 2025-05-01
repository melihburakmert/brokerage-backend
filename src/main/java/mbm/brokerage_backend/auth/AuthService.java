package mbm.brokerage_backend.auth;

public interface AuthService {

    boolean canAccessCustomerData(String customerId);
}
