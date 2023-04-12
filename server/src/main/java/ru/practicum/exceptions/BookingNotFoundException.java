package ru.practicum.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}
