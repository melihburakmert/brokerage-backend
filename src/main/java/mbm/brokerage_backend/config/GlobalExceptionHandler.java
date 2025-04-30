package mbm.brokerage_backend.config;

import mbm.brokerage_backend.common.AssetNotFoundException;
import mbm.brokerage_backend.common.ErrorResponse;
import mbm.brokerage_backend.common.InsufficientAssetsException;
import mbm.brokerage_backend.common.OrderNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(final OrderNotFoundException ex) {
        final ErrorResponse errorResponse = new ErrorResponse()
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .timestamp(Instant.now());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssetNotFoundException(final AssetNotFoundException ex) {
        final ErrorResponse errorResponse = new ErrorResponse()
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .timestamp(Instant.now());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(InsufficientAssetsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAssetsException(final InsufficientAssetsException ex) {
        final ErrorResponse errorResponse = new ErrorResponse()
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .timestamp(Instant.now());

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(final ResponseStatusException ex) {
        final ErrorResponse errorResponse = new ErrorResponse()
                .status(ex.getStatusCode().value())
                .message(ex.getStatusCode().toString())
                .details(ex.getReason())
                .timestamp(Instant.now());

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }
}