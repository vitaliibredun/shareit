package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.constants.BookingStatus;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Integer id;
    private Integer itemId;
    @NotNull(message = "The start of the booking time is equal null")
    @FutureOrPresent(message = "The start of the booking time is before present time")
    private LocalDateTime start;
    @NotNull(message = "The end of the booking time is equal null")
    @FutureOrPresent(message = "The end of the booking time is before present time")
    private LocalDateTime end;
    private Integer booker;
    private BookingStatus status;
}
