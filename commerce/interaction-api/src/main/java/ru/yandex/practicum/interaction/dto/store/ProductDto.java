package ru.yandex.practicum.interaction.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction.dto.enums.ProductCategory;
import ru.yandex.practicum.interaction.dto.enums.ProductState;
import ru.yandex.practicum.interaction.dto.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID productId;

    @NotBlank
    private String productName;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    private ProductCategory productCategory;

    @Min(1)
    @NotNull
    private Float price;

    @NotBlank
    private String description;
}
