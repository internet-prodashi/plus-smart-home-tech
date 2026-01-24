package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Page<OrderDto> getOrderByUsername(
            @Valid @RequestParam String username,
            @RequestParam @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Method getOrderByUsername: username = {}", username);
        return orderService.getOrderByUsername(username, pageable);
    }

    @PutMapping
    public OrderDto createNewOrder(
            @RequestParam String username,
            @Valid @RequestBody CreateNewOrderRequest createOrder
    ) {
        log.info("Method createNewOrder: username = {}, order = {}", username, createOrder);
        return orderService.createNewOrder(username, createOrder);
    }

    @PostMapping("/return")
    public OrderDto returnOrderProducts(@Valid @RequestBody ProductReturnRequest productReturn) {
        log.info("Method returnOrderProducts: ID = {}.", productReturn.getOrderId());
        return orderService.returnOrderProducts(productReturn);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody UUID orderId) {
        log.info("Method paymentOrder: ID = {}.", orderId);
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentOrderFailed(@RequestBody UUID orderId) {
        log.info("Method paymentOrderFailed: orderId = {}.", orderId);
        return orderService.paymentOrderFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        log.info("Method deliveryOrder: orderId = {}.", orderId);
        return orderService.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderFailed(@RequestBody UUID orderId) {
        log.info("Method deliveryOrderFailed: orderId = {}.", orderId);
        return orderService.deliveryOrderFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody UUID orderId) {
        log.info("Method completedOrder: orderId = {}.", orderId);
        return orderService.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) {
        log.info("Method calculateOrderTotalPrice: orderId = {}.", orderId);
        return orderService.calculateOrderTotalPrice(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) {
        log.info("Method calculateOrderDeliveryPrice: orderId = {}.", orderId);
        return orderService.calculateOrderDeliveryPrice(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        log.info("Method assemblyOrder: orderId = {}.", orderId);
        return orderService.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyOrderFailed(@RequestBody UUID orderId) {
        log.info("Method assemblyOrderFailed: orderId = {}.", orderId);
        return orderService.assemblyOrderFailed(orderId);
    }
}