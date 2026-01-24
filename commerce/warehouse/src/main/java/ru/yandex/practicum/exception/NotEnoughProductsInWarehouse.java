package ru.yandex.practicum.exception;

public class NotEnoughProductsInWarehouse extends RuntimeException {
    public NotEnoughProductsInWarehouse(String message) {
        super(message);
    }
}
