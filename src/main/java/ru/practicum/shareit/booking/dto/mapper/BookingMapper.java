package ru.practicum.shareit.booking.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.dto.BookingShortInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.Booker;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingShortInfo toDtoInfo(Booking booking, Item item, User user) {
        return BookingShortInfo
                .builder()
                .id(booking.getId())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(Booker.builder().id(user.getId()).name(user.getName()).build())
                .status(booking.getStatus())
                .build();
    }

    public BookingShortInfo toDtoInfoFromBookingInfo(BookingInfo bookingInfo) {
        return BookingShortInfo
                .builder()
                .id(bookingInfo.getBookingId())
                .item(Item.builder().id(bookingInfo.getItemId()).name(bookingInfo.getName()).build())
                .start(bookingInfo.getStart())
                .end(bookingInfo.getEnd())
                .booker(Booker.builder().id(bookingInfo.getBookerId()).name(bookingInfo.getBookerName()).build())
                .status(bookingInfo.getStatus())
                .build();
    }

    public Booking toModel(BookingDto bookingDto) {
        return Booking
                .builder()
                .id(bookingDto.getId())
                .itemId(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }
}
