package ru.practicum.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.booking.dto.Booker;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingFromRepository;
import ru.practicum.booking.dto.BookingInfo;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(target = "id", source = "bookingFromRepository.bookingId")
    @Mapping(target = "item", source = "bookingFromRepository", qualifiedByName = "createItem")
    @Mapping(target = "booker", source = "bookingFromRepository", qualifiedByName = "createBooker")
    BookingInfo toDto(BookingFromRepository bookingFromRepository);

    @Mapping(target = "booker", source = "booking", qualifiedByName = "createBooker")
    BookingInfo toDto(Booking booking);

    @Mapping(target = "booker", ignore = true)
    Booking toModel(BookingDto bookingDto);

    @Named("createItem")
    default Item createItem(BookingFromRepository bookingFromRepository) {
        return Item
                .builder().id(bookingFromRepository.getItemId()).name(bookingFromRepository.getName()).build();
    }

    @Named("createBooker")
    default Booker createBooker(BookingFromRepository bookingFromRepository) {
        return Booker
                .builder().id(bookingFromRepository.getBookerId()).name(bookingFromRepository.getBookerName()).build();
    }

    @Named("createBooker")
    default Booker createBooker(Booking booking) {
        return Booker
                .builder().id(booking.getBooker().getId()).name(booking.getBooker().getName()).build();
    }
}
