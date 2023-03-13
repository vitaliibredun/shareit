package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.constants.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.Booker;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingShortInfo {
    private Integer id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private BookingStatus status;
}
