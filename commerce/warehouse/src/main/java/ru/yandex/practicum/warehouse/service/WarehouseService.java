package ru.yandex.practicum.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.exception.*;
import ru.yandex.practicum.warehouse.mapper.AddressMapper;
import ru.yandex.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.yandex.practicum.warehouse.model.Address;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.WarehouseAddressRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.interaction.dto.warehouse.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WarehouseService {
    private final WarehouseAddressRepository warehouseAddressRepository;
    private final WarehouseProductRepository warehouseProductRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final AddressMapper addressMapper;
    private final WarehouseProductMapper warehouseProductMapper;
    private final UUID idAddress;

    public WarehouseService(
            WarehouseAddressRepository warehouseAddressRepository,
            WarehouseProductRepository warehouseProductRepository,
            OrderBookingRepository orderBookingRepository,
            AddressMapper addressMapper,
            WarehouseProductMapper warehouseProductMapper
    ) {
        this.warehouseAddressRepository = warehouseAddressRepository;
        this.warehouseProductRepository = warehouseProductRepository;
        this.orderBookingRepository = orderBookingRepository;
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
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseProductRepository
                .findAllById(shoppingCartDto.getProducts().keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        return checkQuantityProductsImpl(shoppingCartDto.getProducts(), warehouseProducts);
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

    @Transactional
    public void shippedProductOnDelivery(ShippedOnDeliveryRequest shippedRequest) {
        OrderBooking orderBooking = orderBookingRepository.findById(shippedRequest.getOrderId())
                .orElseThrow(() -> new NotFoundOrderBookingException("No booking was found for an order with ID = "
                                                                     + shippedRequest.getOrderId()));
        orderBooking.setDeliveryId(shippedRequest.getDeliveryId());
    }

    @Transactional
    public void returnProductToWarehouse(Map<UUID, Integer> products) {
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseProductRepository.findAllById(products.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        List<UUID> missingIds = new ArrayList<>();

        products.forEach((productId, quantity) -> {
            if (!warehouseProducts.containsKey(productId)) missingIds.add(productId);
            else {
                WarehouseProduct warehouseProduct = warehouseProducts.get(productId);
                warehouseProduct.setQuantity(warehouseProduct.getQuantity() + quantity);
            }
        });

        if (!missingIds.isEmpty()) throw new NotFoundProductsOnWarehouseException("There are no products in stock");
    }

    @Transactional
    public BookedProductsDto getProductOnOrderForDelivery(ProductsForOrderRequest assemblyRequest) {
        Map<UUID, Integer> assemblyProducts = assemblyRequest.getProducts();

        Map<UUID, WarehouseProduct> warehouseProducts = warehouseProductRepository
                .findAllById(assemblyProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        BookedProductsDto bookedProducts = checkQuantityProductsImpl(assemblyProducts, warehouseProducts);

        warehouseProducts.forEach((key, value) -> value.setQuantity(value.getQuantity() - assemblyProducts.get(key)));

        orderBookingRepository.save(OrderBooking.builder()
                .products(assemblyProducts)
                .orderId(assemblyRequest.getOrderId())
                .build());

        return bookedProducts;
    }

    private BookedProductsDto checkQuantityProductsImpl(Map<UUID, Integer> cartProducts,
                                                        Map<UUID, WarehouseProduct> warehouseProducts) {
        BookedProductsDto result = BookedProductsDto.builder()
                .deliveryVolume(0.0)
                .deliveryWeight(0.0)
                .fragile(false)
                .build();

        List<ProductNotEnough> productsNotEnough = new ArrayList<>();
        List<UUID> productsNotFound = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : cartProducts.entrySet()) {
            UUID id = entry.getKey();
            Integer wantedCount = entry.getValue();

            if (!warehouseProducts.containsKey(id)) {
                productsNotFound.add(id);
                continue;
            }

            WarehouseProduct product = warehouseProducts.get(id);
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
}
