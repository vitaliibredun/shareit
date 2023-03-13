package ru.practicum.shareit.exceptions;

public class WrongCustomerOfBookingException extends RuntimeException {
    public WrongCustomerOfBookingException(final String message) {
        super(message);
    }
}
