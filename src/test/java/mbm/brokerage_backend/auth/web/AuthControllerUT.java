package mbm.brokerage_backend.auth.web;

import mbm.brokerage_backend.auth.JwtService;
import mbm.brokerage_backend.auth.web.model.AuthenticationRequest;
import mbm.brokerage_backend.auth.web.model.AuthenticationResponse;
import mbm.brokerage_backend.auth.web.model.RegistrationRequest;
import mbm.brokerage_backend.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerUT {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private CustomerService customerService;
    @Mock private PasswordEncoder passwordEncoder;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authenticationManager, jwtService, customerService, passwordEncoder);
    }

    @Test
    void test_login() {
        // GIVEN
        final AuthenticationRequest authenticationRequest = create(AuthenticationRequest.class);
        final Authentication authentication = mock(Authentication.class);
        final UserDetails userDetails = mock(UserDetails.class);
        final String token = create(String.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        // WHEN
        final ResponseEntity<AuthenticationResponse> responseEntity = controller.login(authenticationRequest);

        // THEN
        assertThat(responseEntity).isNotNull().satisfies(response -> {
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getToken()).isEqualTo(token);
        });

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        verify(authentication).getPrincipal();
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void test_register() {
        // GIVEN
        final RegistrationRequest registrationRequest = create(RegistrationRequest.class);
        final String username = registrationRequest.getUsername();
        final String password = registrationRequest.getPassword();
        final String encodedPassword = create(String.class);
        final Authentication authentication = mock(Authentication.class);
        final UserDetails userDetails = mock(UserDetails.class);
        final String token = create(String.class);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(customerService.existsByUsername(username)).thenReturn(false);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        // WHEN
        final ResponseEntity<AuthenticationResponse> responseEntity = controller.register(registrationRequest);

        // THEN
        assertThat(responseEntity).isNotNull().satisfies(response -> {
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getToken()).isEqualTo(token);
        });
        verify(customerService).existsByUsername(username);
        verify(customerService).registerCustomer(username, encodedPassword);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        verify(authentication).getPrincipal();
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void test_register_userAlreadyExists() {
        // GIVEN
        final RegistrationRequest registrationRequest = create(RegistrationRequest.class);
        final String username = registrationRequest.getUsername();

        when(customerService.existsByUsername(username)).thenReturn(true);

        // WHEN
        final ResponseEntity<AuthenticationResponse> responseEntity = controller.register(registrationRequest);

        // THEN
        assertThat(responseEntity).isNotNull().satisfies(
                response -> assertThat(response.getStatusCode().is4xxClientError()).isTrue());

        verify(customerService).existsByUsername(username);
        verifyNoInteractions(authenticationManager, jwtService);
    }

}