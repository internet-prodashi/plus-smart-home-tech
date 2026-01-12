package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class CartController {
    public final CartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Method getShoppingCart: user name {}", username);
        return cartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductInCart(
            @RequestParam String username,
            @RequestBody @NotEmpty Map<UUID, @NotNull @Positive Integer> products
    ) {
        log.info("Method addProductInCart: user name = {} and body product = {}", username, products);
        return cartService.addProductInCart(username, products);
    }

    @DeleteMapping
    public void deactivationCart(@RequestParam String username) {
        log.info("Method deactivationCart: user name {}", username);
        cartService.deactivationCart(username);
    }


    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromCart(
            @RequestParam String username,
            @RequestBody @NotEmpty List<UUID> productsIds
    ) {
        log.info("Method deactivationCart: products Id {} and user name {}", productsIds, username);
        return cartService.removeProductFromCart(username, productsIds);
    }

    @PostMapping("change-quantity")
    public ShoppingCartDto changeQuantityInCart(
            @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest quantityRequest
    ) {
        log.info("Method changeQuantityInCart: user name {}", username);
        return cartService.changeQuantityInCart(username, quantityRequest);
    }
}