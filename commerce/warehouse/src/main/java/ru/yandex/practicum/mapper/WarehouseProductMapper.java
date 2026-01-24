package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseProductMapper {
    WarehouseProduct mapToWarehouseProduct(NewProductInWarehouseRequest request);
}
