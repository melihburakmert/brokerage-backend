package mbm.brokerage_backend.order.repository.mapper;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.repository.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEntityToDtoMapper {

    public List<OrderDto> map(final List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .map(this::map)
                .toList();
    }

    public OrderDto map(final OrderEntity orderDto) {
        return OrderDto.builder()
                .id(orderDto.getId())
                .customerId(orderDto.getCustomerId())
                .assetName(orderDto.getAssetName())
                .orderSide(orderDto.getOrderSide())
                .size(orderDto.getSize())
                .price(orderDto.getPrice())
                .status(orderDto.getStatus())
                .createDate(orderDto.getCreateDate())
                .build();
    }
}
