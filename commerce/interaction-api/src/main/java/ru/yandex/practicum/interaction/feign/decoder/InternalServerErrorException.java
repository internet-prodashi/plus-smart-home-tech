package ru.yandex.practicum.interaction.feign.decoder;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
