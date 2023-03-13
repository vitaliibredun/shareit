package ru.practicum.shareit.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}
