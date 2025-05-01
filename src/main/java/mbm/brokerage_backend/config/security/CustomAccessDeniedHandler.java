package mbm.brokerage_backend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mbm.brokerage_backend.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String MESSAGE = "Forbidden";
    private static final String DETAILS = "You don't have permission to access this resource";

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException {
        final ErrorResponse errorResponse = new ErrorResponse()
                .status(HttpStatus.FORBIDDEN.value())
                .message(MESSAGE)
                .details(DETAILS)
                .timestamp(Instant.now());
                
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/problem+json");
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}