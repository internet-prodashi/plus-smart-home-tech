package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.dto.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}