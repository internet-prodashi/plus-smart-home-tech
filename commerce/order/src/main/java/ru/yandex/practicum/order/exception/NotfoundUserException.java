package ru.yandex.practicum.order.exception;

public class NotfoundUserException extends RuntimeException {
    public NotfoundUserException(String message) {
        super(message);
    }
}
