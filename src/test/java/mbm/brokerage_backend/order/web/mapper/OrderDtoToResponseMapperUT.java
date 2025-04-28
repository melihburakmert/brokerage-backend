package mbm.brokerage_backend.order.web.mapper;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.web.model.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;

class OrderDtoToResponseMapperUT {

    private static final int SIZE = create(Integer.class);

    private OrderDtoToResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderDtoToResponseMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final OrderDto orderDto = create(OrderDto.class);

        // WHEN
        final OrderResponse orderResponse = mapper.map(orderDto);

        // THEN
        assertThat(orderResponse).isNotNull().satisfies(
            response -> {
                assertThat(response.getId()).isEqualTo(orderDto.id());
                assertThat(response.getCustomerId()).isEqualTo(orderDto.customerId());
                assertThat(response.getAssetName()).isEqualTo(orderDto.assetName());
                assertThat(response.getOrderSide().getValue()).isEqualTo(orderDto.orderSide().getValue());
                assertThat(response.getSize()).isEqualTo(orderDto.size());
                assertThat(response.getPrice()).isEqualTo(orderDto.price());
                assertThat(response.getStatus()).isEqualTo(OrderResponse.StatusEnum.fromValue(orderDto.status().getValue()));
                assertThat(response.getCreateDate()).isEqualTo(orderDto.createDate());
            }
        );
    }

    @Test
    void test_mapList() {
        // GIVEN
        final List<OrderDto> orderDtos = ofList(OrderDto.class).size(SIZE).create();

        // WHEN
        final List<OrderResponse> orderResponses = mapper.map(orderDtos);

        // THEN
        assertThat(orderResponses).isNotNull().hasSize(SIZE);
    }

}