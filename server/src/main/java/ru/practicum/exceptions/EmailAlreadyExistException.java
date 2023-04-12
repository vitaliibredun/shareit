package ru.practicum.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(final String message) {
        super(message);
    }
}
