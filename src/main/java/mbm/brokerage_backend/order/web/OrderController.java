package mbm.brokerage_backend.order.web;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.OrderService;
import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.web.api.OrdersApiDelegate;
import mbm.brokerage_backend.order.web.mapper.CreateOrderRequestToDtoMapper;
import mbm.brokerage_backend.order.web.mapper.OrderDtoToResponseMapper;
import mbm.brokerage_backend.order.web.model.CreateOrderRequest;
import mbm.brokerage_backend.order.web.model.OrderResponse;
import mbm.brokerage_backend.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Component
public class OrderController implements OrdersApiDelegate {

    private final CreateOrderRequestToDtoMapper createOrderRequestToDtoMapper;
    private final OrderDtoToResponseMapper orderDtoToResponseMapper;
    private final OrderService orderService;
    private final AuthService authService;

    public OrderController(final CreateOrderRequestToDtoMapper createOrderRequestToDtoMapper,
                           final OrderDtoToResponseMapper orderDtoToResponseMapper,
                           final OrderService orderService,
                           final AuthService authService) {
        this.createOrderRequestToDtoMapper = createOrderRequestToDtoMapper;
        this.orderDtoToResponseMapper = orderDtoToResponseMapper;
        this.orderService = orderService;
        this.authService = authService;
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(final CreateOrderRequest createOrderRequest) {
        final CreateOrderDto createOrderDto = createOrderRequestToDtoMapper.map(createOrderRequest);

        if (!authService.canAccessCustomerData(createOrderDto.customerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        final OrderDto orderDto = orderService.createOrder(createOrderDto);
        final OrderResponse orderResponse = orderDtoToResponseMapper.map(orderDto);
        return ResponseEntity.ok(orderResponse);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> listOrders(final String customerId,
                                                          final Instant fromDate,
                                                          final Instant toDate) {
        validateDateRangeOrder(fromDate, toDate);

        if (!authService.canAccessCustomerData(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        final List<OrderDto> orders = orderService.getOrders(customerId, fromDate, toDate);
        final List<OrderResponse> orderResponses = orderDtoToResponseMapper.map(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @Override
    public ResponseEntity<Void> cancelOrder(final Long orderId) {
        final OrderDto order = orderService.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (!authService.canAccessCustomerData(order.customerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrderResponse> matchOrder(final Long orderId) {
        final OrderDto matchedOrder = orderService.matchOrder(orderId);
        final OrderResponse orderResponse = orderDtoToResponseMapper.map(matchedOrder);
        return ResponseEntity.ok(orderResponse);
    }

    private void validateDateRangeOrder(final Instant fromDate, final Instant toDate) {
        if (fromDate != null && toDate != null && !fromDate.isBefore(toDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fromDate must be before toDate"
            );
        }
    }
}
