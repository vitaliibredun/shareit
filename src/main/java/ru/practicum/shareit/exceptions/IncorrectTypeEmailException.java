package ru.practicum.shareit.exceptions;

public class IncorrectTypeEmailException extends RuntimeException {
    public IncorrectTypeEmailException(final String message) {
        super(message);
    }
}
