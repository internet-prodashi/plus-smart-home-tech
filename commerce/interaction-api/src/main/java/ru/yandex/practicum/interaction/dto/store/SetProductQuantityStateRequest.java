package ru.yandex.practicum.interaction.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}