package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest newRequest) {
        log.info("Method addNewProduct: product = {}", newRequest);
        warehouseService.addNewProduct(newRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Method checkProducts: shopping cart = {}", shoppingCartDto);
        return warehouseService.checkQuantityProducts(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProduct(@Valid @RequestBody AddProductToWarehouseRequest addRequest) {
        log.info("Method addProduct: product = {}", addRequest);
        warehouseService.addQuantityProduct(addRequest);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Method addProduct: getAddress");
        return warehouseService.getAddress();
    }
}