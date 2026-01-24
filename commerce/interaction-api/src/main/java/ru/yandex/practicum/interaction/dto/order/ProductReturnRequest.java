package ru.yandex.practicum.interaction.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProductReturnRequest {
    @NotNull
    private UUID orderId;

    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Integer> products;
}