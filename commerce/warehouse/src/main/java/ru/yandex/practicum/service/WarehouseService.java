package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductNotEnough;
import ru.yandex.practicum.exception.NotEnoughProductsInWarehouse;
import ru.yandex.practicum.exception.NoProductInWarehouseException;
import ru.yandex.practicum.exception.ProductIsAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseAddressRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WarehouseService {
    private final WarehouseAddressRepository warehouseAddressRepository;
    private final WarehouseProductRepository warehouseProductRepository;
    private final AddressMapper addressMapper;
    private final WarehouseProductMapper warehouseProductMapper;
    private final UUID idAddress;

    public WarehouseService(
            WarehouseAddressRepository warehouseAddressRepository,
            WarehouseProductRepository warehouseProductRepository,
            AddressMapper addressMapper,
            WarehouseProductMapper warehouseProductMapper
    ) {
        this.warehouseAddressRepository = warehouseAddressRepository;
        this.warehouseProductRepository = warehouseProductRepository;
        this.addressMapper = addressMapper;
        this.warehouseProductMapper = warehouseProductMapper;
        String[] address = {"ADDRESS_1", "ADDRESS_2"};
        int randomIdx = Random.from(new SecureRandom()).nextInt(0, address.length);
        this.idAddress = warehouseAddressRepository.save(
                Address.builder()
                        .country(address[randomIdx])
                        .city(address[randomIdx])
                        .street(address[randomIdx])
                        .house(address[randomIdx])
                        .flat(address[randomIdx])
                        .build()
        ).getId();
    }

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newRequest) {
        if (warehouseProductRepository.existsById(newRequest.getProductId())) {
            throw new ProductIsAlreadyInWarehouseException("Product with ID = "
                                                           + newRequest.getProductId() + "already registered.");
        }

        WarehouseProduct product = warehouseProductMapper.mapToWarehouseProduct(newRequest);
        warehouseProductRepository.save(product);
    }

    public BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto) {
        Set<UUID> ids = shoppingCartDto.getProducts().keySet();
        Map<UUID, WarehouseProduct> productById = warehouseProductRepository.findAllAsMapByIds(ids);

        BookedProductsDto result = BookedProductsDto.builder()
                .deliveryVolume(0.0)
                .deliveryWeight(0.0)
                .fragile(false)
                .build();

        List<ProductNotEnough> productsNotEnough = new ArrayList<>();
        List<UUID> productsNotFound = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID id = entry.getKey();
            Integer wantedCount = entry.getValue();

            if (!productById.containsKey(id)) {
                productsNotFound.add(id);
                continue;
            }

            WarehouseProduct product = productById.get(id);
            Integer availableCount = product.getQuantity();

            if (wantedCount > availableCount) {
                productsNotEnough.add(new ProductNotEnough(id, availableCount, wantedCount));
                continue;
            }

            Dimension dimension = product.getDimension();

            Double currentVolume = result.getDeliveryVolume();
            Double addVolume = dimension.getHeight() * dimension.getWidth() * dimension.getDepth();
            Double newVolume = currentVolume + addVolume;
            result.setDeliveryVolume(newVolume);

            Double currentWeight = result.getDeliveryWeight();
            Double addWeight = product.getWeight() * wantedCount;
            Double newWeight = currentWeight + addWeight;
            result.setDeliveryWeight(newWeight);

            if (product.getFragile() != null) {
                boolean fragile = result.getFragile() || product.getFragile();
                result.setFragile(fragile);
            }
        }

        if (!productsNotFound.isEmpty())
            throw new NoProductInWarehouseException("There is no information about the products in the warehouse ID: "
                                                    + productsNotFound);
        if (!productsNotEnough.isEmpty())
            throw new NotEnoughProductsInWarehouse("Not enough items in stock: " + productsNotEnough);

        return result;
    }

    @Transactional
    public void addQuantityProduct(AddProductToWarehouseRequest addRequest) {
        WarehouseProduct product = warehouseProductRepository.findById(addRequest.getProductId())
                .orElseThrow(() -> new NoProductInWarehouseException
                        (String.format("Product with ID = %s not in the warehouse", addRequest.getProductId())));
        product.setQuantity(product.getQuantity() + addRequest.getQuantity());
    }

    public AddressDto getAddress() {
        Address address = warehouseAddressRepository.findById(idAddress)
                .orElseThrow(() -> new IllegalStateException("Warehouse address not found, ID = " + idAddress));
        return addressMapper.mapToAddressDto(address);
    }
}
