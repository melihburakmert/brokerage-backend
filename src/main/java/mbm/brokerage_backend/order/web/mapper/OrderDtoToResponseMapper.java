package mbm.brokerage_backend.order.web.mapper;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.web.model.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDtoToResponseMapper {

    public List<OrderResponse> map(final List<OrderDto> orderDtos) {
        return orderDtos.stream()
            .map(this::map)
            .toList();
    }

    public OrderResponse map(final OrderDto orderDto) {
        return new OrderResponse()
            .id(orderDto.id())
            .customerId(orderDto.customerId())
            .assetName(orderDto.assetName())
            .orderSide(mapOrderSide(orderDto))
            .size(orderDto.size())
            .price(orderDto.price())
            .status(mapStatus(orderDto))
            .createDate(orderDto.createDate());
    }

    private OrderResponse.StatusEnum mapStatus(final OrderDto orderDto) {
        return OrderResponse.StatusEnum.fromValue(orderDto.status().getValue());
    }

    private OrderResponse.OrderSideEnum mapOrderSide(final OrderDto orderDto) {
        return OrderResponse.OrderSideEnum.fromValue(orderDto.orderSide().getValue());
    }
}
