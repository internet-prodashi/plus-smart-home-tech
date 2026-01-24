package ru.yandex.practicum.interaction.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;

@Data
@Builder
public class CreateNewOrderRequest {
    @NotNull
    private ShoppingCartDto shoppingCartDto;

    @NotNull
    private AddressDto deliveryAddress;
}