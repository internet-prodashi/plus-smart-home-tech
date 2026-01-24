package ru.yandex.practicum.warehouse.exception;

public class NotFoundProductsOnWarehouseException extends RuntimeException {
    public NotFoundProductsOnWarehouseException(String message) {
        super(message);
    }
}
