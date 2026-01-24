package ru.yandex.practicum.order.exception;

public class NoFoundOrderException extends RuntimeException {
    public NoFoundOrderException(String message) {
        super(message);
    }
}
