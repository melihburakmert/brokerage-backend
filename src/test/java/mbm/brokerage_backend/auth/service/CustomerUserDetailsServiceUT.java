package mbm.brokerage_backend.auth.service;

import mbm.brokerage_backend.customer.CustomerDto;
import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.customer.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceUT {

    private static final String USERNAME = create(String.class);
    private static final String PASSWORD = create(String.class);
    private static final String ROLE_PREFIX = "ROLE_";

    @Mock private CustomerService customerService;

    private CustomerUserDetailsService customerUserDetailsService;

    @BeforeEach
    void setUp() {
        customerUserDetailsService = new CustomerUserDetailsService(customerService);
    }

    @Test
    void test_loadUserByUsername_customerFound() {
        // GIVEN
        final CustomerDto customerDto = CustomerDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .role(Role.CUSTOMER)
                .build();

        when(customerService.findByUsername(USERNAME)).thenReturn(Optional.of(customerDto));

        // WHEN
        final UserDetails userDetails = customerUserDetailsService.loadUserByUsername(USERNAME);

        // THEN
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next())
                .isEqualTo(new SimpleGrantedAuthority(ROLE_PREFIX + Role.CUSTOMER.name()));

        verify(customerService).findByUsername(USERNAME);
    }

    @Test
    void test_loadUserByUsername_adminFound() {
        // GIVEN
        final CustomerDto customerDto = CustomerDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .role(Role.ADMIN)
                .build();

        when(customerService.findByUsername(USERNAME)).thenReturn(Optional.of(customerDto));

        // WHEN
        final UserDetails userDetails = customerUserDetailsService.loadUserByUsername(USERNAME);

        // THEN
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next())
                .isEqualTo(new SimpleGrantedAuthority(ROLE_PREFIX + Role.ADMIN.name()));

        verify(customerService).findByUsername(USERNAME);
    }

    @Test
    void test_loadUserByUsername_customerNotFound() {
        // GIVEN
        when(customerService.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> customerUserDetailsService.loadUserByUsername(USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(USERNAME);

        verify(customerService).findByUsername(USERNAME);
    }
}