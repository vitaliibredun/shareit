package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.constants.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    private Integer id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer booker;
    private Status status;
}
