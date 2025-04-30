package mbm.brokerage_backend.customer.service;

import lombok.RequiredArgsConstructor;
import mbm.brokerage_backend.customer.repository.CustomerRepository;
import mbm.brokerage_backend.customer.repository.entity.CustomerEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String USER_NOT_FOUND_MESSAGE = "Customer %s not found";

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final CustomerEntity customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));

        final List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(ROLE_PREFIX + customer.getRole().name())
        );

        return new User(
                customer.getUsername(),
                customer.getPassword(),
                true,
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
