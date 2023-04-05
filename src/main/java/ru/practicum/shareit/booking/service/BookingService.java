package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingInfo createBooking(Integer userId, BookingDto bookingDto);

    BookingInfo approvingBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingInfo findBooking(Integer userId, Integer bookingId);

    List<BookingInfo> findAllBookingsCustomer(Integer userId, String state, Pageable pageable);

    List<BookingInfo> findAllBookingsOwner(Integer userId, String state, Pageable pageable);
}
