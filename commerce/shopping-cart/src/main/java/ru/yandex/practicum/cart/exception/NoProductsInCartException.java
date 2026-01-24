package ru.yandex.practicum.cart.exception;

public class NoProductsInCartException extends RuntimeException {
    public NoProductsInCartException(String message) {
        super(message);
    }
}
