package ru.yandex.practicum.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@Component
public class WarehouseFallback implements WarehouseFeignClient {
    @Override
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        throw new WarehouseFallbackException("Fallback response: service WAREHOUSE temporarily unavailable");
    }

    @Override
    public BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto) {
        throw new WarehouseFallbackException("Fallback response: service WAREHOUSE temporarily unavailable");
    }

    @Override
    public void addQuantityProduct(AddProductToWarehouseRequest addRequest) {
        throw new WarehouseFallbackException("Fallback response: service WAREHOUSE temporarily unavailable");
    }

    @Override
    public AddressDto getAddress() {
        throw new WarehouseFallbackException("Fallback response: service WAREHOUSE temporarily unavailable");
    }
}