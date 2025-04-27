package mbm.brokerage_backend.order.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.OrderService;
import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.domain.OrderSide;
import mbm.brokerage_backend.order.domain.OrderStatus;
import mbm.brokerage_backend.order.exception.InsufficientAssetsException;
import mbm.brokerage_backend.order.exception.OrderNotFoundException;
import mbm.brokerage_backend.order.repository.OrderRepository;
import mbm.brokerage_backend.order.repository.entity.OrderEntity;
import mbm.brokerage_backend.order.repository.mapper.OrderEntityToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService {

    private static final String TRY = "TRY";

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
        validateAndUpdateAssetUsableSize(createOrderDto);

        final OrderEntity order = buildOrderEntity(createOrderDto);
        final OrderEntity savedOrder = orderRepository.save(order);
        return orderEntityToDtoMapper.map(savedOrder);
    }


    @Override
    public List<OrderDto> getOrders(final String customerId, final Instant fromDate, final Instant toDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, fromDate, toDate).stream()
                .map(orderEntityToDtoMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(final Long orderId) {
        final OrderEntity order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING).orElseThrow(() -> new OrderNotFoundException(orderId));
        updateAssetUsableSize(order);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
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

    private void validateAndUpdateAssetUsableSize(final CreateOrderDto createOrderDto) {
        final String customerId = createOrderDto.customerId();
        final String assetName = createOrderDto.orderSide() == OrderSide.BUY ? TRY : createOrderDto.assetName();
        final BigDecimal requiredAmount = calculateOrderValue(createOrderDto.orderSide(), createOrderDto.price(), createOrderDto.size());

        final AssetDto asset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);

        if (asset.usableSize().compareTo(requiredAmount) < 0) {
            throw new InsufficientAssetsException(customerId, assetName, requiredAmount, asset.usableSize());
        }

        final BigDecimal newUsableSize = asset.usableSize().subtract(requiredAmount);
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);
    }

    private void updateAssetUsableSize(final OrderEntity order) {
        final String customerId = order.getCustomerId();
        final String assetName = order.getOrderSide() == OrderSide.BUY ? TRY : order.getAssetName();
        final BigDecimal amountToReturn = calculateOrderValue(order.getOrderSide(), order.getPrice(), order.getSize());

        final AssetDto asset = assetService.getAssetByCustomerIdAndAssetName(
                customerId, assetName);

        final BigDecimal newUsableSize = asset.usableSize().add(amountToReturn);
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);
    }

    private BigDecimal calculateOrderValue(final OrderSide orderSide, final BigDecimal orderPrice, final BigDecimal orderSize) {
        return orderSide == OrderSide.BUY ? orderPrice.multiply(orderSize) : orderSize;
    }
}
