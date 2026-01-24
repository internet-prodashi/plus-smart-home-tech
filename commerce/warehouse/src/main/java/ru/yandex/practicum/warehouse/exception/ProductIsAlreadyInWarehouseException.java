package ru.yandex.practicum.warehouse.exception;

public class ProductIsAlreadyInWarehouseException extends RuntimeException {
    public ProductIsAlreadyInWarehouseException(String message) {
        super(message);
    }
}
