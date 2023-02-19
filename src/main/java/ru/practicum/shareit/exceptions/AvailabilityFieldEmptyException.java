package ru.practicum.shareit.exceptions;

public class AvailabilityFieldEmptyException extends RuntimeException {
    public AvailabilityFieldEmptyException(final String message) {
        super(message);
    }
}
