package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShippedOnDeliveryRequest {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID deliveryId;
}