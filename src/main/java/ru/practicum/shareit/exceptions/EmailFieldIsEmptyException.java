package ru.practicum.shareit.exceptions;

public class EmailFieldIsEmptyException extends RuntimeException {
    public EmailFieldIsEmptyException(final String message) {
        super(message);
    }
}
