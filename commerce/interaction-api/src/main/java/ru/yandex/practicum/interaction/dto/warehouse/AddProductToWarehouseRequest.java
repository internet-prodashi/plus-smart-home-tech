package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddProductToWarehouseRequest {
    private UUID productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
