package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortInfo;

import java.util.List;

public interface BookingService {
    BookingShortInfo createBooking(Integer userId, BookingDto bookingDto);

    BookingShortInfo approvingBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingShortInfo findBooking(Integer userId, Integer bookingId);

    List<BookingShortInfo> findAllBookingsCustomer(Integer userId, String state);

    List<BookingShortInfo> findAllBookingsOwner(Integer userId, String state);
}
