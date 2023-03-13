package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LastBooking {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime time;
}
