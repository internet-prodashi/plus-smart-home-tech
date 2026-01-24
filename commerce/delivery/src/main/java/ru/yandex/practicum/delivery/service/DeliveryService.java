package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.exception.DeliveryNoFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.warehouse.ShippedOnDeliveryRequest;
import ru.yandex.practicum.interaction.dto.enums.DeliveryState;
import ru.yandex.practicum.interaction.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final WarehouseFeignClient warehouseClient;
    private final OrderFeignClient orderClient;

    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        return deliveryMapper.mapToDeliveryDto(deliveryRepository.save(deliveryMapper.mapToDelivery(deliveryDto)));
    }

    @Transactional
    public void changeStatusDeliveryOnDelivered(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.deliveryOrder(delivery.getOrderID());
    }

    @Transactional
    public void pickedProductsOnDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        warehouseClient.shippedProductOnDelivery(ShippedOnDeliveryRequest.builder()
                .orderId(delivery.getOrderID())
                .deliveryId(deliveryId)
                .build());
    }

    @Transactional
    public void changeStatusDeliveryOnFailed(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryOrderFailed(delivery.getOrderID());
    }

    public Double calculationCoastDelivery(OrderDto orderDto) {
        Delivery delivery = getDeliveryById(orderDto.getDeliveryId());

        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();

        if (fromAddress.getStreet() == null || toAddress.getStreet() == null)
            throw new IllegalArgumentException("The address cannot be null");

        double warehouseMarkup = 1.0;
        if (fromAddress.toString().contains("ADDRESS_2")) warehouseMarkup = 2.0;

        double deliveryCost = 5 + (5 * warehouseMarkup);

        if (orderDto.getFragile()) deliveryCost += deliveryCost * 0.2;

        deliveryCost += orderDto.getDeliveryWeight() * 0.3 + orderDto.getDeliveryVolume() * 0.2;

        String fromStreet = fromAddress.getStreet().trim().toLowerCase();
        String toStreet = toAddress.getStreet().trim().toLowerCase();
        if (!fromStreet.equals(toStreet)) deliveryCost += deliveryCost * 0.2;

        return deliveryCost;
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNoFoundException("Delivery with ID = " + deliveryId + " not found"));
    }
}