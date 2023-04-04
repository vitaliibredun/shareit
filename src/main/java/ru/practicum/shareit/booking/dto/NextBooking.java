package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NextBooking {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime time;
}
