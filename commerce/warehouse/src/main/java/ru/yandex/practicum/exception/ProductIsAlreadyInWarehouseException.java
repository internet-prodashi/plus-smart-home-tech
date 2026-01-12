package ru.yandex.practicum.exception;

public class ProductIsAlreadyInWarehouseException extends RuntimeException {
    public ProductIsAlreadyInWarehouseException(String message) {
        super(message);
    }
}
