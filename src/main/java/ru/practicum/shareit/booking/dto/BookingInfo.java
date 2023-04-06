package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfo {
    private Integer id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private BookingStatus status;
}
