package ru.practicum.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LastBooking {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime time;
}
