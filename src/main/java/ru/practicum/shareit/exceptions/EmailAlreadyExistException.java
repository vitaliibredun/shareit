package ru.practicum.shareit.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(final String message) {
        super(message);
    }
}
