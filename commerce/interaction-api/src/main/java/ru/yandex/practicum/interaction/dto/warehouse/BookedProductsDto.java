package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedProductsDto {
    @NotNull
    @Positive
    private Double deliveryWeight;

    @NotNull
    @Positive
    private Double deliveryVolume;

    @NotNull
    private Boolean fragile;
}