package mbm.brokerage_backend.order;

import mbm.brokerage_backend.order.domain.CreateOrderDto;

import java.time.Instant;
import java.util.List;

public interface OrderService {

    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrders(String customerId, Instant fromDate, Instant toDate);

    OrderDto createOrder(CreateOrderDto createOrderDto);

    void cancelOrder(Long orderId);

    OrderDto matchOrder(Long orderId);
}
