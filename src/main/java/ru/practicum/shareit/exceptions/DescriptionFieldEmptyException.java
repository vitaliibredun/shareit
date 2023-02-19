package ru.practicum.shareit.exceptions;

public class DescriptionFieldEmptyException extends RuntimeException {
    public DescriptionFieldEmptyException(final String message) {
        super(message);
    }
}
