package ru.yandex.practicum.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createNewDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        log.info("Method createNewDelivery: deliveryDto = {}", deliveryDto);
        return deliveryService.createNewDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void changeStatusDeliveryOnDelivered(@Valid @RequestBody UUID deliveryId) {
        log.info("Method changeStatusDeliveryOnDelivered. ID delivery = {}.", deliveryId);
        deliveryService.changeStatusDeliveryOnDelivered(deliveryId);
    }

    @PostMapping("/picked")
    public void pickedProductsOnDelivery(@Valid @RequestBody UUID deliveryId) {
        log.info("Method pickedProductsOnDelivery. ID delivery = {}.", deliveryId);
        deliveryService.pickedProductsOnDelivery(deliveryId);
    }

    @PostMapping("/failed")
    public void changeStatusDeliveryOnFailed(@Valid @RequestBody UUID deliveryId) {
        log.info("Method changeStatusDeliveryOnFailed. ID delivery = {}.", deliveryId);
        deliveryService.changeStatusDeliveryOnFailed(deliveryId);
    }

    @PostMapping("/cost")
    public Double calculationCoastDelivery(@Valid @RequestBody OrderDto orderDto) {
        log.info("Method calculationCoastDelivery. orderDto = {}.", orderDto);
        return deliveryService.calculationCoastDelivery(orderDto);
    }
}