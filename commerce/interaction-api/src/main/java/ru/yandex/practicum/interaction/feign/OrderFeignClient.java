package ru.yandex.practicum.interaction.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderFeignClient {
    @GetMapping
    Page<OrderDto> getOrderByUsername(
            @Valid @RequestParam String username,
            @RequestParam @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC) Pageable pageable
    ) throws FeignException;

    @PutMapping
    OrderDto createNewOrder(@RequestParam String username,
                            @Valid @RequestBody CreateNewOrderRequest createOrder) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrderProducts(@Valid @RequestBody ProductReturnRequest productReturn) throws FeignException;

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto paymentOrderFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody UUID orderId) throws FeignException;
}
