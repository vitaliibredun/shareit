package ru.practicum.shareit.exceptions;

public class BookerIsOwnerOfItemException extends RuntimeException {
    public BookerIsOwnerOfItemException(final String message) {
        super(message);
    }
}
