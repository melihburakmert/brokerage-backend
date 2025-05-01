package mbm.brokerage_backend.auth.service;

import mbm.brokerage_backend.auth.AuthService;
import mbm.brokerage_backend.customer.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceUT {

    private static final String CUSTOMER_ID = "customer123";
    private static final String DIFFERENT_CUSTOMER_ID = "customer456";

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImp();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void test_canAccessCustomerData_whenUserIsCustomerOwner() {
        // GIVEN
        mockAuthenticateUser(CUSTOMER_ID, false);

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void test_canAccessCustomerData_whenUserIsAdmin() {
        // GIVEN
        mockAuthenticateUser(DIFFERENT_CUSTOMER_ID, true);

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void test_canAccessCustomerData_whenUserIsNotAuthorized() {
        // GIVEN
        mockAuthenticateUser(DIFFERENT_CUSTOMER_ID, false);

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void test_canAccessCustomerData_whenNoAuthentication() {
        // GIVEN

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void test_canAccessCustomerData_whenAuthenticationIsNull() {
        // GIVEN
        SecurityContextHolder.clearContext();

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void test_canAccessCustomerData_whenUserIsNotAuthenticated() {
        // GIVEN
        mockUnauthenticatedUser();

        // WHEN
        final boolean result = authService.canAccessCustomerData(CUSTOMER_ID);

        // THEN
        assertThat(result).isFalse();
    }

    private void mockAuthenticateUser(final String username, final boolean isAdmin) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        final List<SimpleGrantedAuthority> authorities;
        if (isAdmin) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()));
        } else {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_" + Role.CUSTOMER.name()));
        }

        final UserDetails userDetails = new User(username, "password", authorities);
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockUnauthenticatedUser() {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, null, null);
        authentication.setAuthenticated(false);

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
