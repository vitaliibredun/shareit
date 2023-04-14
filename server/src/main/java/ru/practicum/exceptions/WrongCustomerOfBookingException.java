package ru.practicum.exceptions;

public class WrongCustomerOfBookingException extends RuntimeException {
    public WrongCustomerOfBookingException(final String message) {
        super(message);
    }
}
