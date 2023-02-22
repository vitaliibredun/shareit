package ru.practicum.shareit.exceptions;

public class WrongOwnerOfItemException extends RuntimeException {
    public WrongOwnerOfItemException(final String message) {
        super(message);
    }
}
