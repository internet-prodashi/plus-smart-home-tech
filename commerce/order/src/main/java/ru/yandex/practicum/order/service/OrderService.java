package ru.yandex.practicum.order.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.dto.warehouse.ProductsForOrderRequest;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.enums.DeliveryState;
import ru.yandex.practicum.interaction.dto.enums.OrderState;
import ru.yandex.practicum.order.exception.NoFoundOrderException;
import ru.yandex.practicum.order.exception.NotfoundUserException;
import ru.yandex.practicum.interaction.feign.DeliveryFeignClient;
import ru.yandex.practicum.interaction.feign.PaymentFeignClient;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;
import ru.yandex.practicum.order.mapper.AddressMapper;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Address;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.AddressRepository;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final AddressMapper addressMapper;
    private final OrderMapper orderMapper;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentFeignClient paymentClient;
    private final WarehouseFeignClient warehouseClient;
    private final DeliveryFeignClient deliveryClient;

    public Page<OrderDto> getOrderByUsername(String username, Pageable pageable) {
        checkUsernameForEmpty(username);
        Page<Order> orders = orderRepository.findByUsername(username, pageable);
        return orders.map(orderMapper::mapToOrderDto);
    }

    @Transactional
    public OrderDto createNewOrder(String username, CreateNewOrderRequest createOrder) {
        checkUsernameForEmpty(username);

        BookedProductsDto bookedProductsDto = warehouseClient.checkQuantityProducts(createOrder.getShoppingCartDto());
        Address address = addressRepository.save(addressMapper.mapToAddress(createOrder.getDeliveryAddress()));

        Order newOrder = Order.builder()
                .shoppingCartId(createOrder.getShoppingCartDto().getCartId())
                .products(createOrder.getShoppingCartDto().getProducts())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.getFragile())
                .username(username)
                .address(address)
                .build();

        return orderMapper.mapToOrderDto(orderRepository.save(newOrder));
    }

    @Transactional
    public OrderDto returnOrderProducts(ProductReturnRequest productReturn) {
        Order order = getOrderById(productReturn.getOrderId());

        if (order.getState() == OrderState.PRODUCT_RETURNED || order.getState() == OrderState.CANCELED)
            throw new ValidationException("Order with ID = " + order.getOrderId() + " refunded or cancelled");

        if (productReturn.getProducts().isEmpty()) throw new ValidationException("List of products is empty.");

        warehouseClient.returnProductToWarehouse(productReturn.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.mapToOrderDto(order);
    }

    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.ON_PAYMENT) {
            order.setState(OrderState.PAID);
            return orderMapper.mapToOrderDto(order);
        }

        if (order.getState() == OrderState.PAID) return orderMapper.mapToOrderDto(order);

        if (order.getState() != OrderState.ASSEMBLED)
            throw new ValidationException("order with ID = " + orderId + " is being collected");

        PaymentDto paymentDto = paymentClient.makingPaymentForOrder(orderMapper.mapToOrderDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);

        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto paymentOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.ON_DELIVERY) {
            order.setState(OrderState.DELIVERED);
            return orderMapper.mapToOrderDto(order);
        }

        if (order.getState() == OrderState.DELIVERED) return orderMapper.mapToOrderDto(order);

        if (order.getState() != OrderState.PAID)
            throw new ValidationException("Delivery is carried out only by prepayment. The order with ID = "
                                          + orderId + " has not been paid.");

        deliveryClient.pickedProductsInDelivery(order.getDeliveryId());
        order.setState(OrderState.ON_DELIVERY);

        return orderMapper.mapToOrderDto(order);
    }

    @Transactional
    public OrderDto deliveryOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto completedOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto calculateOrderTotalPrice(UUID orderId) {
        Order order = getOrderById(orderId);

        Double productsPrice = paymentClient.calculateProductCostPayment(orderMapper.mapToOrderDto(order));
        order.setProductPrice(productsPrice);

        Double totalPrice = paymentClient.calculateTotalCostPayment(orderMapper.mapToOrderDto(order));
        order.setTotalPrice(totalPrice);

        return orderMapper.mapToOrderDto(order);
    }

    @Transactional
    public OrderDto calculateOrderDeliveryPrice(UUID orderId) {
        Order order = getOrderById(orderId);

        DeliveryDto deliveryDto = deliveryClient.createNewDelivery(DeliveryDto.builder()
                .fromAddress(warehouseClient.getAddress())
                .toAddress(addressMapper.mapToAddressDto(order.getAddress()))
                .orderID(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build());

        order.setDeliveryId(deliveryDto.getDeliveryId());

        Double deliveryPrice = deliveryClient.calculationCoastDelivery(orderMapper.mapToOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        return orderMapper.mapToOrderDto(order);
    }

    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.NEW) {
            warehouseClient.getProductOnOrderForDelivery(ProductsForOrderRequest.builder()
                    .products(order.getProducts())
                    .orderId(orderId)
                    .build());
            order.setState(OrderState.ASSEMBLED);
        } else throw new ValidationException("An order with ID = "
                                             + orderId + " cannot be sent for assembly. Create a new order.");

        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto assemblyOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    private void checkUsernameForEmpty(String username) {
        if (username == null || username.isBlank())
            throw new NotfoundUserException("Not found user with name = " + username);
    }

    private Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoFoundOrderException("Order with ID = " + id + " not found"));
    }
}