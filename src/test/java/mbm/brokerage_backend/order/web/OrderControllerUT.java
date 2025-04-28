package mbm.brokerage_backend.order.web;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.OrderService;
import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.web.mapper.CreateOrderRequestToDtoMapper;
import mbm.brokerage_backend.order.web.mapper.OrderDtoToResponseMapper;
import mbm.brokerage_backend.order.web.model.CreateOrderRequest;
import mbm.brokerage_backend.order.web.model.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerUT {

    private static final int SIZE = create(Integer.class);

    @Mock private CreateOrderRequestToDtoMapper createOrderRequestToDtoMapper;
    @Mock private OrderDtoToResponseMapper orderDtoToResponseMapper;
    @Mock private OrderService orderService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(createOrderRequestToDtoMapper, orderDtoToResponseMapper, orderService);
    }

    @Test
    void test_createOrder() {
        // GIVEN
        final CreateOrderRequest createOrderRequest = create(CreateOrderRequest.class);
        final CreateOrderDto createOrderDto = create(CreateOrderDto.class);
        final OrderDto orderDto = create(OrderDto.class);
        final OrderResponse orderResponse = create(OrderResponse.class);

        when(createOrderRequestToDtoMapper.map(createOrderRequest)).thenReturn(createOrderDto);
        when(orderService.createOrder(createOrderDto)).thenReturn(orderDto);
        when(orderDtoToResponseMapper.map(orderDto)).thenReturn(orderResponse);

        // WHEN
        final ResponseEntity<OrderResponse> response = orderController.createOrder(createOrderRequest);

        // THEN
        assertThat(response).isNotNull().satisfies(orderResponseEntity -> {
            assertThat(orderResponseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(orderResponseEntity.getBody()).isNotNull().isEqualTo(orderResponse);
        });
    }

    @Test
    void test_listOrders() {
        // GIVEN
        final String customerId = create(String.class);
        final Instant fromDate = Instant.now().minusSeconds(create(Long.class));
        final Instant toDate = Instant.now();
        final List<OrderDto> orderDtos = ofList(OrderDto.class).size(SIZE).create();
        final List<OrderResponse> orderResponses = ofList(OrderResponse.class).size(SIZE).create();

        when(orderService.getOrders(customerId, fromDate, toDate)).thenReturn(orderDtos);
        when(orderDtoToResponseMapper.map(orderDtos)).thenReturn(orderResponses);

        // WHEN
        final ResponseEntity<List<OrderResponse>> response = orderController.listOrders(customerId, fromDate, toDate);

        // THEN
        assertThat(response).isNotNull().satisfies(orderResponse -> {
            assertThat(orderResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(orderResponse.getBody()).isNotNull().isEqualTo(orderResponses);
        });
    }

    @Test
    void test_listOrders_invalidDateRange() {
        // GIVEN
        final String customerId = create(String.class);
        final Instant fromDate = Instant.now();
        final Instant toDate = Instant.now().minusSeconds(create(Long.class));

        // WHEN & THEN
        assertThatThrownBy(() -> orderController.listOrders(customerId, fromDate, toDate))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("fromDate must be before toDate");
    }

    @Test
    void test_listOrders_nullDateRange() {
        // GIVEN
        final String customerId = create(String.class);
        final Instant toDate = Instant.now();

        // WHEN
        final ResponseEntity<List<OrderResponse>> response = orderController.listOrders(customerId, null, toDate);

        // THEN
        assertThat(response).isNotNull().satisfies(orderResponse -> {
            assertThat(orderResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(orderResponse.getBody()).isNotNull();
        });
    }

    @Test
    void test_listOrders_nullDateRange_both() {
        // GIVEN
        final String customerId = create(String.class);

        // WHEN
        final ResponseEntity<List<OrderResponse>> response = orderController.listOrders(customerId, null, null);

        // THEN
        assertThat(response).isNotNull().satisfies(orderResponse -> {
            assertThat(orderResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(orderResponse.getBody()).isNotNull();
        });
    }

    @Test
    void test_listOrders_ResponseStatusException() {
        // GIVEN
        final String customerId = create(String.class);
        final Instant fromDate = Instant.now();
        final Instant toDate = fromDate.minusSeconds(1); // Invalid range

        // WHEN & THEN
        assertThatThrownBy(() -> orderController.listOrders(customerId, fromDate, toDate))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("fromDate must be before toDate");
    }

    @Test
    void test_cancelOrder() {
        // GIVEN
        final Long orderId = create(Long.class);
        doNothing().when(orderService).cancelOrder(orderId);

        // WHEN
        final ResponseEntity<Void> response = orderController.cancelOrder(orderId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(orderService).cancelOrder(orderId);
    }

    @Test
    void test_matchOrder() {
        // GIVEN
        final Long orderId = create(Long.class);
        final OrderDto matchedOrder = create(OrderDto.class);
        final OrderResponse orderResponse = create(OrderResponse.class);

        when(orderService.matchOrder(orderId)).thenReturn(matchedOrder);
        when(orderDtoToResponseMapper.map(matchedOrder)).thenReturn(orderResponse);

        // WHEN
        final ResponseEntity<OrderResponse> response = orderController.matchOrder(orderId);

        // THEN
        assertThat(response).isNotNull().satisfies(orderResponseEntity -> {
            assertThat(orderResponseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(orderResponseEntity.getBody()).isNotNull().isEqualTo(orderResponse);
        });
    }
}