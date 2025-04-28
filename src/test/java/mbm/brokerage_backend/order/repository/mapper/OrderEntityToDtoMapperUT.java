package mbm.brokerage_backend.order.repository.mapper;

import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.repository.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.ofList;

class OrderEntityToDtoMapperUT {

    private static final int SIZE = create(Integer.class);

    private OrderEntityToDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderEntityToDtoMapper();
    }

    @Test
    void test_map() {
        // GIVEN
        final OrderEntity orderEntity = create(OrderEntity.class);

        // WHEN
        final OrderDto result = mapper.map(orderEntity);

        // THEN
        assertThat(result).isNotNull().satisfies(orderDto -> {
            assertThat(orderDto.id()).isEqualTo(orderEntity.getId());
            assertThat(orderDto.customerId()).isEqualTo(orderEntity.getCustomerId());
            assertThat(orderDto.assetName()).isEqualTo(orderEntity.getAssetName());
            assertThat(orderDto.orderSide()).isEqualTo(orderEntity.getOrderSide());
            assertThat(orderDto.size()).isEqualTo(orderEntity.getSize());
            assertThat(orderDto.price()).isEqualTo(orderEntity.getPrice());
            assertThat(orderDto.status()).isEqualTo(orderEntity.getStatus());
            assertThat(orderDto.createDate()).isEqualTo(orderEntity.getCreateDate());
        });
    }

    @Test
    void test_mapList() {
        // GIVEN
        final List<OrderEntity> orderEntities = ofList(OrderEntity.class).size(SIZE).create();

        // WHEN
        final List<OrderDto> orderDtos = mapper.map(orderEntities);

        // THEN
        assertThat(orderDtos).isNotNull().hasSize(SIZE);
    }

}