package ru.practicum.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final String message) {
        super(message);
    }
}
