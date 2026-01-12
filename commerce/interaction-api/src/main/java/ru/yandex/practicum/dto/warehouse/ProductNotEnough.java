package ru.yandex.practicum.dto.warehouse;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductNotEnough {
    private UUID productId;

    private Integer availableCount;

    private Integer wantedCount;

    private Integer differenceCount;

    public ProductNotEnough(UUID productId, Integer availableCount, Integer wantedCount) {
        this.productId = productId;
        this.availableCount = availableCount;
        this.wantedCount = wantedCount;
        this.differenceCount = wantedCount - availableCount;
    }
}
