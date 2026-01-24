package ru.yandex.practicum.warehouse.exception;

public class NoProductInWarehouseException extends RuntimeException {
    public NoProductInWarehouseException(String message) {
        super(message);
    }
}
