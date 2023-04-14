package ru.practicum.booking.validation;


import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

public interface BookingValidation {
    Item checkItemData(User user, BookingDto bookingDto);

    Booking checkBookingData(Integer userId, Integer bookingId);

    Booking checkIfStatusAlreadyApproved(Integer userId, Integer bookingId, Boolean approved);
}
