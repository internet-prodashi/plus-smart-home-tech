package ru.yandex.practicum.interaction.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ShoppingCartDto {
    @NotNull
    private UUID cartId;

    @NotNull
    private Map<UUID, @NotNull @Positive Integer> products;
}