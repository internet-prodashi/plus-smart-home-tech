package ru.yandex.practicum.payment.exception;

public class NoFoundPaymentException extends RuntimeException {
    public NoFoundPaymentException(String message) {
        super(message);
    }
}
