package mbm.brokerage_backend.order.web.mapper;

import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.web.model.CreateOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;

class CreateOrderRequestToDtoMapperUT {

    private CreateOrderRequestToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreateOrderRequestToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final CreateOrderRequest request = create(CreateOrderRequest.class);

        // WHEN
        final CreateOrderDto result = mapper.map(request);

        // THEN
        assertThat(result).isNotNull().satisfies(createOrderDto -> {
            assertThat(createOrderDto.customerId()).isEqualTo(request.getCustomerId());
            assertThat(createOrderDto.assetName()).isEqualTo(request.getAssetName());
            assertThat(createOrderDto.orderSide().getValue()).isEqualTo(request.getOrderSide().getValue());
            assertThat(createOrderDto.size()).isEqualTo(request.getSize());
            assertThat(createOrderDto.price()).isEqualTo(request.getPrice());
        });
    }

}