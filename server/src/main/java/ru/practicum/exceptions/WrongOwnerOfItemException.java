package ru.practicum.exceptions;

public class WrongOwnerOfItemException extends RuntimeException {
    public WrongOwnerOfItemException(final String message) {
        super(message);
    }
}
