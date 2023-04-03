package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromRepository;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.dto.Booker;

@Mapper
public interface BookingMapper {
    default BookingInfo toDto(BookingFromRepository bookingFromRepository) {
        if (bookingFromRepository == null) {
            return null;
        }

        BookingInfo.BookingInfoBuilder infoBuilder = BookingInfo.builder();

        infoBuilder.id(bookingFromRepository.getBookingId());
        infoBuilder.item(Item.builder().id(bookingFromRepository.getItemId()).name(bookingFromRepository.getName()).build());
        infoBuilder.start(bookingFromRepository.getStart());
        infoBuilder.end(bookingFromRepository.getEnd());
        infoBuilder.booker(Booker.builder().id(bookingFromRepository.getBookerId()).name(bookingFromRepository.getBookerName()).build());
        infoBuilder.status(bookingFromRepository.getStatus());

        return infoBuilder.build();
    }

    default BookingInfo toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingInfo.BookingInfoBuilder infoBuilder = BookingInfo.builder();

        infoBuilder.id(booking.getId());
        infoBuilder.item(booking.getItem());
        infoBuilder.start(booking.getStart());
        infoBuilder.end(booking.getEnd());
        infoBuilder.booker(Booker.builder().id(booking.getBooker().getId()).name(booking.getBooker().getName()).build());
        infoBuilder.status(booking.getStatus());

        return infoBuilder.build();
    }

    default Booking toModel(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }

        Booking.BookingBuilder bookingBuilder = Booking.builder();

        bookingBuilder.id(bookingDto.getId());
        bookingBuilder.start(bookingDto.getStart());
        bookingBuilder.end(bookingDto.getEnd());
        bookingBuilder.status(bookingDto.getStatus());

        return bookingBuilder.build();
    }
}
