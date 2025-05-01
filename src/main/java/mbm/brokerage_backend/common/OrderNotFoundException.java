package mbm.brokerage_backend.common;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends RuntimeException {

    private static final String DETAILS = "Order with id %d not found";
    private static final String MESSAGE = "Order not found";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private final Long orderId;

    public OrderNotFoundException(final Long orderId) {
        this.orderId = orderId;
    }

    public String getDetails() {
        return String.format(DETAILS, orderId);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    public HttpStatus getStatus() {
        return STATUS;
    }
}
