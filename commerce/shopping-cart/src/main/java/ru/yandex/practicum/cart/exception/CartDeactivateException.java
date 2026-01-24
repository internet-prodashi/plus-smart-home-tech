package ru.yandex.practicum.cart.exception;

public class CartDeactivateException extends RuntimeException {
    public CartDeactivateException(String message) {
        super(message);
    }
}
