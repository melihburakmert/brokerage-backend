package mbm.brokerage_backend.common;

import org.springframework.http.HttpStatus;

public class AssetNotFoundException extends RuntimeException {

    private static final String DETAILS = "Asset '%s' not found for customer %s";
    private static final String MESSAGE = "Asset not found";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private final String customerId;
    private final String assetName;


    public AssetNotFoundException(final String customerId, final String assetName) {
        this.customerId = customerId;
        this.assetName = assetName;
    }

    public String getDetails() {
        return String.format(DETAILS, assetName, customerId);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    public HttpStatus getStatus() {
        return STATUS;
    }
}
