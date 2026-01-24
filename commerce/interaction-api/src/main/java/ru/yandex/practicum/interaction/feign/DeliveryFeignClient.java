package ru.yandex.practicum.interaction.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {
    @PutMapping
    DeliveryDto createNewDelivery(@Valid @RequestBody DeliveryDto deliveryDto) throws FeignException;

    @PostMapping("/successful")
    void changeStatusDeliveryOnDelivered(@Valid @RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/picked")
    void pickedProductsInDelivery(@Valid @RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/failed")
    void changeStatusDeliveryOnFailed(@Valid @RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/cost")
    Double calculationCoastDelivery(@Valid @RequestBody OrderDto orderDto) throws FeignException;
}