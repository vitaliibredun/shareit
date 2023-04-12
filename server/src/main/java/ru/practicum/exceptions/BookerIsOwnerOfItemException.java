package ru.practicum.exceptions;

public class BookerIsOwnerOfItemException extends RuntimeException {
    public BookerIsOwnerOfItemException(final String message) {
        super(message);
    }
}
