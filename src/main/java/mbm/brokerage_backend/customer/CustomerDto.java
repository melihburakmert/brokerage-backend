package mbm.brokerage_backend.customer;

import lombok.Builder;

@Builder
public record CustomerDto(String username, String password, Role role) {
}