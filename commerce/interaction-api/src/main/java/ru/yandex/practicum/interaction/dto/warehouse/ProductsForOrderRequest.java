package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProductsForOrderRequest {
    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Integer> products;

    @NotNull
    private UUID orderId;
}