package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

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

    @PostMapping("/shipped")
    public void shippedProductOnDelivery(@Valid @RequestBody ShippedOnDeliveryRequest shippedRequest) {
        log.info("Перeдаем заказ {} в доставку {}", shippedRequest.getOrderId(), shippedRequest.getDeliveryId());
        warehouseService.shippedProductOnDelivery(shippedRequest);
        log.info("Заказ {} передали в доставку {} УСПЕШНО.", shippedRequest.getOrderId(), shippedRequest.getDeliveryId());
    }

    @PostMapping("/return")
    public void returnProductToWarehouse(@RequestBody Map<UUID, @NotNull @Positive Integer> products) {
        log.info("Начинаем возврат товара {} на склад.", products);
        warehouseService.returnProductToWarehouse(products);
        log.info("Возврат товара {} прошел УСПЕШНО", products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto getProductOnOrderForDelivery(@Valid @RequestBody ProductsForOrderRequest assemblyRequest) {
        log.info("Начинаем сборку товара {} к заказу {} для подготовки к отправке.",
                assemblyRequest.getProducts(), assemblyRequest.getOrderId());
        return warehouseService.getProductOnOrderForDelivery(assemblyRequest);
    }
}