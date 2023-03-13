package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingValidation {
    void checkIfItemAvailable(Integer itemId);

    void checkTimeBooking(BookingDto bookingDto);

    void checkCustomerOfBooking(Integer userId, Integer bookingId);

    void checkIfStatusAlreadyApproved(Integer userId, Integer bookingId, Boolean approved);

    void checkIfCustomerIsOwnerOfBooking(Integer userId, BookingDto bookingDto);
}
