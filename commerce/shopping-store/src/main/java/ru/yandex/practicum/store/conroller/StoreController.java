package ru.yandex.practicum.store.conroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.dto.store.ProductListDto;
import ru.yandex.practicum.interaction.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.dto.enums.ProductCategory;
import ru.yandex.practicum.interaction.dto.enums.QuantityState;
import ru.yandex.practicum.store.service.StoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public ProductListDto getAllProducts(
            @RequestParam ProductCategory category,
            Pageable pageable
    ) {
        log.info("Method getAllProducts: category = {} и pageable = {}", category, pageable);
        return storeService.getAllProducts(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Method createProduct: product name = {}", productDto.getProductName());
        return storeService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Method updateProduct: product name = {}", productDto.getProductName());
        return storeService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProductById(@RequestBody UUID productId) {
        log.info("Method removeProductById: product with Id = {}", productId);
        return storeService.removeProductById(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState
    ) {
        SetProductQuantityStateRequest request = SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build();

        log.info("Method setProductQuantityState: product with Id = {}", productId);
        return storeService.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Method getProductById: product with  ID = {}", productId);
        return storeService.getProductById(productId);
    }
}