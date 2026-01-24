package ru.yandex.practicum.delivery.exception;

public class DeliveryNoFoundException extends RuntimeException {
    public DeliveryNoFoundException(String message) {
        super(message);
    }
}
