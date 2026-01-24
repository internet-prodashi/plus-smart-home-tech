package ru.yandex.practicum.interaction.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.ProductsForOrderRequest;
import ru.yandex.practicum.interaction.dto.warehouse.ShippedOnDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseFallback.class)
public interface WarehouseFeignClient {
    @PutMapping
    void newProduct(@Valid @RequestBody NewProductInWarehouseRequest newRequest) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkQuantityProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void addQuantityProduct(@Valid @RequestBody AddProductToWarehouseRequest addRequest) throws FeignException;

    @GetMapping("/address")
    AddressDto getAddress() throws FeignException;

    @PostMapping("/shipped")
    void shippedProductOnDelivery(@Valid @RequestBody ShippedOnDeliveryRequest shippedRequest) throws FeignException;

    @PostMapping("/return")
    void returnProductToWarehouse(@RequestBody Map<UUID, @NotNull @Positive Integer> products) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto getProductOnOrderForDelivery(
            @Valid @RequestBody ProductsForOrderRequest assemblyRequest) throws FeignException;
}