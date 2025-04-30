package mbm.brokerage_backend.common;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InsufficientAssetsException extends RuntimeException {

    private static final String DETAILS = "Customer %s has insufficient %s balance for order. Required: %s, Available: %s";
    private static final String MESSAGE = "Insufficient assets";
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private final String customerId;
    private final String assetName;
    private final BigDecimal required;
    private final BigDecimal available;

    public InsufficientAssetsException(final String customerId, final String assetName, final BigDecimal required, final BigDecimal available) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.required = required;
        this.available = available;
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    public String getDetails() {
        return String.format(DETAILS, customerId, assetName, required, available);
    }

    public HttpStatus getStatus() {
        return STATUS;
    }
}