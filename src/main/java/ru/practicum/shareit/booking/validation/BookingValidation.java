package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface BookingValidation {
    Item checkItemData(User user, BookingDto bookingDto);

    Booking checkCustomerOfBooking(Integer userId, Integer bookingId);

    Booking checkIfStatusAlreadyApproved(Integer userId, Integer bookingId, Boolean approved);
}
