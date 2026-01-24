package ru.yandex.practicum.warehouse.exception;

public class NotEnoughProductsInWarehouse extends RuntimeException {
    public NotEnoughProductsInWarehouse(String message) {
        super(message);
    }
}
