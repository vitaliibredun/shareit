package ru.practicum.booking.dto;

import lombok.*;
import ru.practicum.booking.constants.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingFromRepository {
    private Integer bookingId;
    private Integer itemId;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
    private String  bookerName;
    private BookingStatus status;
}
