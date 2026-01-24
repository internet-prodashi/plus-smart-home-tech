package ru.yandex.practicum.payment.exception;

public class ImpossibleCalculateCostOrderException extends RuntimeException {
    public ImpossibleCalculateCostOrderException(String message) {
        super(message);
    }
}
