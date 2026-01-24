package ru.yandex.practicum.interaction.feign;

public class WarehouseFallbackException extends RuntimeException {
    public WarehouseFallbackException(String message) {
        super(message);
    }
}
