package ru.yandex.practicum.interaction.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    @NotNull(message = "orderId не может быть NULL.")
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    @NotEmpty
    private Map<@NotNull UUID, @NotNull @Positive Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    private OrderState state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private Double totalPrice;

    private Double deliveryPrice;

    private Double productPrice;
}