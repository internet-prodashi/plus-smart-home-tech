package ru.yandex.practicum.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.cart.model.ShoppingCart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {
    ShoppingCartDto mapToCartDto(ShoppingCart shoppingCart);
}