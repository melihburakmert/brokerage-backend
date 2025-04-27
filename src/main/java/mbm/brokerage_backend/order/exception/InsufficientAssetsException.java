package mbm.brokerage_backend.order.exception;

import java.math.BigDecimal;

public class InsufficientAssetsException extends RuntimeException {

    private static final String MESSAGE = "Customer %s has insufficient %s balance for order. Required: %s, Available: %s";

    public InsufficientAssetsException(final String customerId, final String assetName, final BigDecimal required, final BigDecimal available) {
        super(String.format(MESSAGE, customerId, assetName, required, available));
    }
}