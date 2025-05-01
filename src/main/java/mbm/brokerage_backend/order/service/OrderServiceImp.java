package mbm.brokerage_backend.order.service;

import mbm.brokerage_backend.asset.AssetDto;
import mbm.brokerage_backend.asset.AssetService;
import mbm.brokerage_backend.common.CustomerNotFoundException;
import mbm.brokerage_backend.customer.CustomerService;
import mbm.brokerage_backend.order.OrderDto;
import mbm.brokerage_backend.order.OrderService;
import mbm.brokerage_backend.order.domain.CreateOrderDto;
import mbm.brokerage_backend.order.domain.OrderSide;
import mbm.brokerage_backend.order.domain.OrderStatus;
import mbm.brokerage_backend.common.InsufficientAssetsException;
import mbm.brokerage_backend.common.OrderNotFoundException;
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
    private final CustomerService  customerService;
    private final OrderRepository orderRepository;
    private final OrderEntityToDtoMapper orderEntityToDtoMapper;

    public OrderServiceImp(final AssetService assetService,
                           final CustomerService customerService,
                           final OrderRepository orderRepository,
                           final OrderEntityToDtoMapper orderEntityToDtoMapper) {
        this.assetService = assetService;
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.orderEntityToDtoMapper = orderEntityToDtoMapper;
    }

    @Override
    public OrderDto getOrder(final Long orderId) {
        final OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderEntityToDtoMapper.map(orderEntity);
    }

    @Override
    public List<OrderDto> getOrders(final String customerId, final Instant fromDate, final Instant toDate) {
        validateCustomerExists(customerId);
        final List<OrderEntity> orderEntities = getOrderEntities(customerId, fromDate, toDate);

        return orderEntityToDtoMapper.map(orderEntities);
    }

    @Override
    @Transactional
    public OrderDto createOrder(final CreateOrderDto createOrderDto) {
        final String customerId = createOrderDto.customerId();
        validateCustomerExists(customerId);

        final String assetName = createOrderDto.orderSide() == OrderSide.BUY ? TRY : createOrderDto.assetName();
        final BigDecimal requiredAmount = calculateOrderValue(createOrderDto.orderSide(), createOrderDto.price(), createOrderDto.size());

        final AssetDto asset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);

        validateSufficientUsableAssetSize(asset, requiredAmount, customerId, assetName);

        final BigDecimal newUsableSize = asset.usableSize().subtract(requiredAmount);
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);

        final OrderEntity order = buildOrderEntity(createOrderDto);
        final OrderEntity savedOrder = orderRepository.save(order);
        return orderEntityToDtoMapper.map(savedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(final Long orderId) {
        final OrderEntity order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        final String customerId = order.getCustomerId();
        final String assetName = order.getOrderSide() == OrderSide.BUY ? TRY : order.getAssetName();
        final BigDecimal amountToReturn = calculateOrderValue(order.getOrderSide(), order.getPrice(), order.getSize());

        final AssetDto asset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);

        final BigDecimal newUsableSize = asset.usableSize().add(amountToReturn);
        assetService.updateAssetUsableSize(customerId, assetName, newUsableSize);

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderDto matchOrder(final Long orderId) {
        final OrderEntity order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        final String customerId = order.getCustomerId();
        final String assetName = order.getAssetName();
        final BigDecimal orderSize = order.getSize();
        final BigDecimal orderPrice = order.getPrice();

        if (order.getOrderSide() == OrderSide.BUY) {
            matchBuyOrder(customerId, assetName, orderSize, orderPrice);
        } else {
            matchSellOrder(customerId, assetName, orderSize, orderPrice);
        }

        order.setStatus(OrderStatus.MATCHED);
        final OrderEntity savedOrder = orderRepository.save(order);
        return orderEntityToDtoMapper.map(savedOrder);
    }

    private void matchBuyOrder(final String customerId, final String assetName, final BigDecimal orderSize, final BigDecimal orderPrice) {
        final AssetDto tryAsset = assetService.getAssetByCustomerIdAndAssetName(customerId, TRY);

        final AssetDto boughtAsset;
        if (!assetService.isAssetExists(customerId, assetName)) {
            boughtAsset = assetService.initializeAsset(customerId, assetName);
        } else {
            boughtAsset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);
        }

        final BigDecimal totalCost = orderPrice.multiply(orderSize);

        validateSufficientAssetSize(tryAsset, totalCost, customerId, TRY);

        assetService.updateAssetSize(customerId, TRY, tryAsset.size().subtract(totalCost));

        assetService.updateAssetSizeAndUsableSize(
            customerId, assetName,
            boughtAsset.size().add(orderSize),
            boughtAsset.usableSize().add(orderSize)
        );
    }

    private void matchSellOrder(final String customerId, final String assetName, final BigDecimal orderSize, final BigDecimal orderPrice) {
        final AssetDto sellAsset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);
        final AssetDto tryAsset = assetService.getAssetByCustomerIdAndAssetName(customerId, TRY);

        validateSufficientAssetSize(sellAsset, orderSize, customerId, assetName);

        assetService.updateAssetSize(customerId, assetName, sellAsset.size().subtract(orderSize));

        final BigDecimal totalProceeds = orderPrice.multiply(orderSize);
        assetService.updateAssetSizeAndUsableSize(
            customerId, TRY,
            tryAsset.size().add(totalProceeds),
            tryAsset.usableSize().add(totalProceeds)
        );
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

    private BigDecimal calculateOrderValue(final OrderSide orderSide, final BigDecimal orderPrice, final BigDecimal orderSize) {
        return orderSide == OrderSide.BUY ? orderPrice.multiply(orderSize) : orderSize;
    }

    private void validateSufficientAssetSize(final AssetDto asset, final BigDecimal required, final String customerId, final String assetName) {
        if (asset.size().compareTo(required) < 0) {
            throw new InsufficientAssetsException(customerId, assetName, required, asset.size());
        }
    }

    private void validateSufficientUsableAssetSize(final AssetDto asset, final BigDecimal required, final String customerId, final String assetName) {
        if (asset.usableSize().compareTo(required) < 0) {
            throw new InsufficientAssetsException(customerId, assetName, required, asset.usableSize());
        }
    }

    private List<OrderEntity> getOrderEntities(final String customerId, final Instant fromDate, final Instant toDate) {
        // TODO: Add criteria api or specification to handle this later
        if (fromDate != null && toDate != null) {
            return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, fromDate, toDate);
        }
        else if (fromDate == null && toDate == null) {
            return orderRepository.findByCustomerId(customerId);
        }
        else if (fromDate != null) {
            return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, fromDate, Instant.now());
        } else {
            return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, Instant.EPOCH, toDate);
        }
    }

    private void validateCustomerExists(final String customerId) {
        if (!customerService.existsByUsername(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
