package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductListDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.StoreRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {
    private final StoreRepository storeRepository;
    private final ProductMapper mapper;

    public ProductListDto getAllProducts(ProductCategory category, Pageable pageable) {
        List<Product> products = storeRepository.findByProductCategory(category, pageable);
        List<ProductDto> productsDto = products.stream().map(mapper::mapToProductDto).toList();
        return new ProductListDto(productsDto, pageable.getSort());
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        return mapper.mapToProductDto(storeRepository.save(mapper.mapToProduct(productDto)));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = getProduct(productDto.getProductId());
        mapper.updateProductFromDto(product, productDto);
        return mapper.mapToProductDto(product);
    }

    @Transactional
    public Boolean removeProductById(UUID productId) {
        Product product = getProduct(productId);

        if (product.getProductState().equals(ProductState.DEACTIVATE)) return false;

        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest stateRequest) {
        Product product = getProduct(stateRequest.getProductId());
        product.setQuantityState(stateRequest.getQuantityState());
        return true;
    }

    public ProductDto getProductById(UUID productId) {
        Product product = getProduct(productId);
        return mapper.mapToProductDto(product);
    }

    private Product getProduct(UUID productId) {
        return storeRepository.findById(productId).
                orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " NOT FOUND"));
    }
}