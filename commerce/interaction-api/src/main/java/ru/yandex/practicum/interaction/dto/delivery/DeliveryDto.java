package ru.yandex.practicum.interaction.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.enums.DeliveryState;

import java.util.UUID;

@Data
@Builder
public class DeliveryDto {
    @NotNull
    private UUID deliveryId;

    @NotNull
    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    @NotNull
    private UUID orderID;

    @NotNull
    private DeliveryState deliveryState;
}