package mbm.brokerage_backend.auth.web;

import mbm.brokerage_backend.auth.web.api.AuthApiDelegate;
import mbm.brokerage_backend.auth.web.model.AuthenticationRequest;
import mbm.brokerage_backend.auth.web.model.AuthenticationResponse;
import mbm.brokerage_backend.auth.web.model.RegistrationRequest;
import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.auth.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthController implements AuthApiDelegate {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerService customerService;

    public AuthController(
            final AuthenticationManager authenticationManager,
            final JwtService jwtService,
            final CustomerService customerService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(final AuthenticationRequest request) {
        final Authentication authentication = authenticateUser(request.getUsername(), request.getPassword());

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> register(final RegistrationRequest request) {
        final String username = request.getUsername();
        final String password = request.getPassword();

        if (customerService.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        customerService.registerCustomer(username, password);
        final Authentication authentication = authenticateUser(username, password);

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthenticationResponse(jwtToken));
    }

    private Authentication authenticateUser(final String username, final String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password
                )
        );
    }
}