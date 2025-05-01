package mbm.brokerage_backend.auth.service;

import mbm.brokerage_backend.customer.Role;
import mbm.brokerage_backend.auth.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImp implements AuthService {

    @Override
    public boolean canAccessCustomerData(final String customerId) {
        final String username = getAuthenticatedUsername();
        
        if (username == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }
        
        return username.equals(customerId);
    }

    private String getAuthenticatedUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof final UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return principal.toString();
    }


    private boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    private boolean hasRole(final Role role) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role.name()));
    }
}