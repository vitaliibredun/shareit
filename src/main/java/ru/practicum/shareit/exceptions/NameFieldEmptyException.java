package ru.practicum.shareit.exceptions;

public class NameFieldEmptyException extends RuntimeException {
    public NameFieldEmptyException(final String message) {
        super(message);
    }
}
