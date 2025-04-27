package mbm.brokerage_backend.order.service;

import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.OrderService;
import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.domain.OrderStatus;
import mbm.brokerage_backend.order.repository.OrderRepository;
import mbm.brokerage_backend.order.repository.entity.OrderEntity;
import mbm.brokerage_backend.order.repository.mapper.OrderEntityToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService {

    private final AssetService assetService;
    private final OrderRepository orderRepository;
    private final OrderEntityToDtoMapper orderEntityToDtoMapper;

    public OrderServiceImp(final AssetService assetService, final OrderRepository orderRepository, final OrderEntityToDtoMapper orderEntityToDtoMapper) {
        this.assetService = assetService;
        this.orderRepository = orderRepository;
        this.orderEntityToDtoMapper = orderEntityToDtoMapper;
    }

    @Override
    @Transactional
    public OrderDto createOrder(final CreateOrderDto createOrderDto) {
        // TODO: Check TRY asset
        final OrderEntity order = buildOrderEntity(createOrderDto);
        final OrderEntity savedOrder = orderRepository.save(order);
        return orderEntityToDtoMapper.map(savedOrder);

    }

    @Override
    public List<OrderDto> getOrders(final String customerId, final Instant fromDate, final Instant toDate) {
        // TODO: Validate date range params
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, fromDate, toDate).stream()
                .map(orderEntityToDtoMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(final Long orderId) {
        // TODO: Update TRY asset
        orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .ifPresent(this::cancelOrder);
        // TODO: Handle order not found case
    }

    @Override
    @Transactional
    public OrderDto matchOrder(final Long orderId) {
        // TODO - Bonus
        return null;
    }

    private OrderEntity buildOrderEntity(final CreateOrderDto createOrderDto) {
        return OrderEntity.builder()
                .customerId(createOrderDto.customerId())
                .assetName(createOrderDto.assetName())
                .orderSide(createOrderDto.orderSide())
                .size(createOrderDto.size())
                .price(createOrderDto.price())
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();
    }

    private void cancelOrder(final OrderEntity order) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
