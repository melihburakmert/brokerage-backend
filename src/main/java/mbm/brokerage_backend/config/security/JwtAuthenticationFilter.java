package mbm.brokerage_backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mbm.brokerage_backend.auth.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(final JwtService jwtService, final UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Skip JWT processing for requests without valid Authorization header
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        final String username = jwtService.extractUsername(jwt);

        if (isAuthenticationRequired(username)) {
            authenticateUser(request, jwt, username);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticationRequired(final String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUser(final HttpServletRequest request, final String jwt, final String username) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
            final UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(final UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}