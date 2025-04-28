package mbm.brokerage_backend.order.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    PENDING("PENDING"),
    MATCHED("MATCHED"),
    CANCELED("CANCELED");

    private final String value;

    OrderStatus(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static OrderStatus fromValue(final String value) {
        return Arrays.stream(OrderStatus.values())
                .filter(orderStatus -> orderStatus.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid OrderStatus value: " + value));
    }
}