package ru.yandex.practicum.warehouse.exception;

public class NotFoundOrderBookingException extends RuntimeException {
    public NotFoundOrderBookingException(String message) {
        super(message);
    }
}
