package mbm.brokerage_backend.order.web.mapper;

import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.domain.OrderSide;
import mbm.brokerage_backend.order.web.model.CreateOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderRequestToDtoMapper {

    public CreateOrderDto map(final CreateOrderRequest request) {

        return CreateOrderDto.builder()
            .customerId(request.getCustomerId())
            .assetName(request.getAssetName())
            .orderSide(mapOrderSide(request))
            .size(request.getSize())
            .price(request.getPrice())
            .build();
    }

    private OrderSide mapOrderSide(final CreateOrderRequest createOrderRequest) {
        return OrderSide.fromValue(createOrderRequest.getOrderSide().getValue());
    }
}
