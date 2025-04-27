package mbm.brokerage_backend.order.exception;

public class OrderNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Order with id %d not found.";

    public OrderNotFoundException(final Long orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
