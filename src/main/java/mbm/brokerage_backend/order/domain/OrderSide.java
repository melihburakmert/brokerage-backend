package mbm.brokerage_backend.order.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderSide {
    BUY("BUY"),
    SELL("SELL");

    private final String value;

    OrderSide(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static OrderSide fromValue(final String value) {
        return Arrays.stream(OrderSide.values())
            .filter(orderSide -> orderSide.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid OrderSide value: " + value));
    }
}