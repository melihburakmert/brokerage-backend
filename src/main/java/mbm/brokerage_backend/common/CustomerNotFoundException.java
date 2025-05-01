package mbm.brokerage_backend.common;

import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends RuntimeException {
    private static final String DETAILS = "Customer '%s' not found";
    private static final String MESSAGE = "Customer not found";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private final String customerId;

    public CustomerNotFoundException(final String customerId) {
        this.customerId = customerId;
    }

    public String getDetails() {
        return String.format(DETAILS, customerId);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    public HttpStatus getStatus() {
        return STATUS;
    }
}
