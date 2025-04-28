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
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.of;
import static org.instancio.Instancio.ofList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceUT {

    private static final int SIZE = 3;
    private static final String ASSET_NAME = "INGA";
    private static final String TRY_ASSET = "TRY";
    private static final String CUSTOMER_ID = create(String.class);
    private static final long ORDER_ID = 123L;

    @Mock private AssetService assetService;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderEntityToDtoMapper orderEntityToDtoMapper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImp(assetService, orderRepository, orderEntityToDtoMapper);
    }

    @Test
    void test_getOrders() {
        // GIVEN
        final Instant fromDate = Instant.now().minusSeconds(create(Long.class));
        final Instant toDate = Instant.now();
        final List<OrderEntity> orderEntities = ofList(OrderEntity.class).size(SIZE).create();
        final List<OrderDto> orderDtos = ofList(OrderDto.class).size(SIZE).create();

        when(orderRepository.findByCustomerIdAndCreateDateBetween(CUSTOMER_ID, fromDate, toDate)).thenReturn(orderEntities);
        when(orderEntityToDtoMapper.map(orderEntities)).thenReturn(orderDtos);

        // WHEN
        final List<OrderDto> result = orderService.getOrders(CUSTOMER_ID, fromDate, toDate);

        // THEN
        assertThat(result).isNotNull().hasSize(orderDtos.size()).isEqualTo(orderDtos);
        verify(orderRepository).findByCustomerIdAndCreateDateBetween(CUSTOMER_ID, fromDate, toDate);
        verify(orderEntityToDtoMapper).map(orderEntities);
    }

    @Test
    void test_createOrder_buy_withSufficientAssets() {
        // GIVEN
        final OrderSide orderSide = OrderSide.BUY;
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal requiredAmount = orderPrice.multiply(orderSize); // 60000

        final CreateOrderDto createOrderDto = of(CreateOrderDto.class)
                .set(Select.field(CreateOrderDto::customerId), CUSTOMER_ID)
                .set(Select.field(CreateOrderDto::assetName), ASSET_NAME)
                .set(Select.field(CreateOrderDto::orderSide), orderSide)
                .set(Select.field(CreateOrderDto::size), orderSize)
                .set(Select.field(CreateOrderDto::price), orderPrice)
                .create();

        final AssetDto tryAsset = of(AssetDto.class)
        .set(Select.field(AssetDto::size), BigDecimal.valueOf(100000))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(80000))
                .create();

        final OrderEntity orderEntity = create(OrderEntity.class);
        final OrderDto expectedOrderDto = create(OrderDto.class);

        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderEntityToDtoMapper.map(orderEntity)).thenReturn(expectedOrderDto);

        // WHEN
        final OrderDto result = orderService.createOrder(createOrderDto);

        // THEN
        assertThat(result).isEqualTo(expectedOrderDto);

        verify(assetService).updateAssetUsableSize(CUSTOMER_ID, TRY_ASSET,
                tryAsset.usableSize().subtract(requiredAmount));

        final ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(orderCaptor.capture());

        final OrderEntity capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(capturedOrder.getAssetName()).isEqualTo(ASSET_NAME);
        assertThat(capturedOrder.getOrderSide()).isEqualTo(OrderSide.BUY);
        assertThat(capturedOrder.getSize()).isEqualTo(orderSize);
        assertThat(capturedOrder.getPrice()).isEqualTo(orderPrice);
        assertThat(capturedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(capturedOrder.getCreateDate()).isNotNull();

        verify(orderEntityToDtoMapper).map(orderEntity);
    }

    @Test
    void test_createOrder_sell_withSufficientAssets() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);

        final CreateOrderDto createOrderDto = of(CreateOrderDto.class)
                .set(Select.field(CreateOrderDto::customerId), CUSTOMER_ID)
                .set(Select.field(CreateOrderDto::assetName), ASSET_NAME)
                .set(Select.field(CreateOrderDto::orderSide), OrderSide.SELL)
                .set(Select.field(CreateOrderDto::size), orderSize)
                .set(Select.field(CreateOrderDto::price), orderPrice)
                .create();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(5))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(3))
                .create();

        final OrderEntity orderEntity = create(OrderEntity.class);
        final OrderDto expectedOrderDto = create(OrderDto.class);

        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderEntityToDtoMapper.map(orderEntity)).thenReturn(expectedOrderDto);

        // WHEN
        final OrderDto result = orderService.createOrder(createOrderDto);

        // THEN
        assertThat(result).isEqualTo(expectedOrderDto);

        verify(assetService).updateAssetUsableSize(CUSTOMER_ID, ASSET_NAME,
                btcAsset.usableSize().subtract(orderSize));

        final ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(orderCaptor.capture());

        final OrderEntity capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(capturedOrder.getAssetName()).isEqualTo(ASSET_NAME);
        assertThat(capturedOrder.getOrderSide()).isEqualTo(OrderSide.SELL);
        assertThat(capturedOrder.getSize()).isEqualTo(orderSize);
        assertThat(capturedOrder.getPrice()).isEqualTo(orderPrice);
        assertThat(capturedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(capturedOrder.getCreateDate()).isNotNull();

        verify(orderEntityToDtoMapper).map(orderEntity);
    }

    @Test
    void test_createOrder_buy_withInsufficientAssets_shouldThrowException() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(5);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal requiredAmount = orderPrice.multiply(orderSize); // 150000

        final CreateOrderDto createOrderDto = of(CreateOrderDto.class)
                .set(Select.field(CreateOrderDto::customerId), CUSTOMER_ID)
                .set(Select.field(CreateOrderDto::assetName), ASSET_NAME)
                .set(Select.field(CreateOrderDto::orderSide), OrderSide.BUY)
                .set(Select.field(CreateOrderDto::size), orderSize)
                .set(Select.field(CreateOrderDto::price), orderPrice)
                .create();

        final AssetDto tryAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(100000)) // Not enough funds
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(80000))
                .create();

        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.createOrder(createOrderDto))
                .isInstanceOf(InsufficientAssetsException.class)
                .hasMessageContaining(CUSTOMER_ID)
                .hasMessageContaining(TRY_ASSET)
                .hasMessageContaining(requiredAmount.toString());

        verify(assetService).getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET);
        verify(assetService, never()).updateAssetUsableSize(anyString(), anyString(), any(BigDecimal.class));
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderEntityToDtoMapper);
    }

    @Test
    void test_createOrder_sell_withInsufficientAssets_shouldThrowException() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(3);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);

        final CreateOrderDto createOrderDto = of(CreateOrderDto.class)
                .set(Select.field(CreateOrderDto::customerId), CUSTOMER_ID)
                .set(Select.field(CreateOrderDto::assetName), ASSET_NAME)
                .set(Select.field(CreateOrderDto::orderSide), OrderSide.SELL)
                .set(Select.field(CreateOrderDto::size), orderSize)
                .set(Select.field(CreateOrderDto::price), orderPrice)
                .create();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(2)) // Not enough BTC
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(2))
                .create();

        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.createOrder(createOrderDto))
                .isInstanceOf(InsufficientAssetsException.class)
                .hasMessageContaining(CUSTOMER_ID)
                .hasMessageContaining(ASSET_NAME)
                .hasMessageContaining(orderSize.toString());

        verify(assetService).getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME);
        verify(assetService, never()).updateAssetUsableSize(anyString(), anyString(), any(BigDecimal.class));
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderEntityToDtoMapper);
    }

    @Test
    void test_cancelOrder_withPendingOrder_shouldCancelOrder() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(5);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal amountToReturn = orderPrice.multiply(orderSize); // 150000

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.BUY)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto tryAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(200000))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(50000))
                .create();

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

        // WHEN
        orderService.cancelOrder(ORDER_ID);

        // THEN
        verify(assetService).updateAssetUsableSize(CUSTOMER_ID, TRY_ASSET,
                tryAsset.usableSize().add(amountToReturn));

        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.CANCELED));
    }

    @Test
    void test_cancelOrder_withNonExistentOrder_shouldThrowException() {
        // GIVEN
        final Long orderId = 999L;

        when(orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());

        verifyNoInteractions(assetService);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void test_cancelOrder_sellOrder_shouldReturnAssetAmount() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.SELL)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(10))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(5))
                .create();

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);

        // WHEN
        orderService.cancelOrder(ORDER_ID);

        // THEN
        verify(assetService).updateAssetUsableSize(CUSTOMER_ID, ASSET_NAME,
                btcAsset.usableSize().add(orderSize));

        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.CANCELED));
    }

    @Test
    void test_matchOrder_buyOrder_withSufficientAssets() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal totalCost = orderPrice.multiply(orderSize); // 60000

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.BUY)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto tryAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(100000))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(40000))
                .create();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(5))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(3))
                .create();

        final OrderDto expectedOrderDto = create(OrderDto.class);

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderEntityToDtoMapper.map(orderEntity)).thenReturn(expectedOrderDto);

        // WHEN
        final OrderDto result = orderService.matchOrder(ORDER_ID);

        // THEN
        assertThat(result).isEqualTo(expectedOrderDto);

        verify(orderRepository).findByIdAndStatus(ORDER_ID, OrderStatus.PENDING);

        verify(assetService).updateAssetSize(CUSTOMER_ID, TRY_ASSET,
                tryAsset.size().subtract(totalCost));

        verify(assetService).updateAssetSizeAndUsableSize(
                CUSTOMER_ID, ASSET_NAME,
                btcAsset.size().add(orderSize),
                btcAsset.usableSize().add(orderSize));

        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.MATCHED));

        verify(orderEntityToDtoMapper).map(orderEntity);
    }

    @Test
    void test_matchOrder_sellOrder_withSufficientAssets() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal totalProceeds = orderPrice.multiply(orderSize); // 60000

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.SELL)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(5))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(3))
                .create();

        final AssetDto tryAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(100000))
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(100000))
                .create();

        final OrderDto expectedOrderDto = create(OrderDto.class);

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderEntityToDtoMapper.map(orderEntity)).thenReturn(expectedOrderDto);

        // WHEN
        final OrderDto result = orderService.matchOrder(ORDER_ID);

        // THEN
        assertThat(result).isEqualTo(expectedOrderDto);

        verify(orderRepository).findByIdAndStatus(ORDER_ID, OrderStatus.PENDING);

        verify(assetService).updateAssetSize(CUSTOMER_ID, ASSET_NAME,
                btcAsset.size().subtract(orderSize));

        verify(assetService).updateAssetSizeAndUsableSize(
                CUSTOMER_ID, TRY_ASSET,
                tryAsset.size().add(totalProceeds),
                tryAsset.usableSize().add(totalProceeds));

        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.MATCHED));

        verify(orderEntityToDtoMapper).map(orderEntity);
    }

    @Test
    void test_matchOrder_buyOrder_withInsufficientAssets_shouldThrowException() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(2);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);
        final BigDecimal totalCost = orderPrice.multiply(orderSize); // 60000

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.BUY)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto tryAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(50000)) // Not enough TRY for the purchase
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(40000))
                .create();

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.matchOrder(ORDER_ID))
                .isInstanceOf(InsufficientAssetsException.class)
                .hasMessageContaining(CUSTOMER_ID)
                .hasMessageContaining(TRY_ASSET)
                .hasMessageContaining(totalCost.toString());

        verify(orderRepository).findByIdAndStatus(ORDER_ID, OrderStatus.PENDING);
        verify(assetService).getAssetByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET);
        verify(assetService, never()).updateAssetSize(anyString(), anyString(), any(BigDecimal.class));
        verify(assetService, never()).updateAssetSizeAndUsableSize(anyString(), anyString(),
                any(BigDecimal.class), any(BigDecimal.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verifyNoInteractions(orderEntityToDtoMapper);
    }

    @Test
    void test_matchOrder_sellOrder_withInsufficientAssets_shouldThrowException() {
        // GIVEN
        final BigDecimal orderSize = BigDecimal.valueOf(5);
        final BigDecimal orderPrice = BigDecimal.valueOf(30000);

        final OrderEntity orderEntity = OrderEntity.builder()
                .customerId(CUSTOMER_ID)
                .assetName(ASSET_NAME)
                .orderSide(OrderSide.SELL)
                .size(orderSize)
                .price(orderPrice)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        final AssetDto btcAsset = of(AssetDto.class)
                .set(Select.field(AssetDto::size), BigDecimal.valueOf(3)) // Not enough BTC to sell
                .set(Select.field(AssetDto::usableSize), BigDecimal.valueOf(2))
                .create();

        when(orderRepository.findByIdAndStatus(ORDER_ID, OrderStatus.PENDING)).thenReturn(Optional.of(orderEntity));
        when(assetService.getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME)).thenReturn(btcAsset);

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.matchOrder(ORDER_ID))
                .isInstanceOf(InsufficientAssetsException.class)
                .hasMessageContaining(CUSTOMER_ID)
                .hasMessageContaining(ASSET_NAME)
                .hasMessageContaining(orderSize.toString());

        verify(orderRepository).findByIdAndStatus(ORDER_ID, OrderStatus.PENDING);
        verify(assetService).getAssetByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME);
        verify(assetService, never()).updateAssetSize(anyString(), anyString(), any(BigDecimal.class));
        verify(assetService, never()).updateAssetSizeAndUsableSize(anyString(), anyString(),
                any(BigDecimal.class), any(BigDecimal.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verifyNoInteractions(orderEntityToDtoMapper);
    }

    @Test
    void test_matchOrder_nonExistentOrder_shouldThrowException() {
        // GIVEN
        final Long orderId = 999L;

        when(orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> orderService.matchOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());

        verifyNoInteractions(assetService);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderEntityToDtoMapper);
    }
}