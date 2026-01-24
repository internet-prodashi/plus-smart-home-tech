package ru.yandex.practicum.feign;

public class WarehouseFallbackException extends RuntimeException {
    public WarehouseFallbackException(String message) {
        super(message);
    }
}
