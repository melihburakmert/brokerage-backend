package mbm.brokerage_backend.asset.exception;

public class AssetNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer %s does not have asset %s";

    public AssetNotFoundException(final String customerId, final String assetName) {
        super(String.format(MESSAGE, customerId, assetName));
    }
}
