package ru.yandex.practicum.interaction.feign.decoder;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
