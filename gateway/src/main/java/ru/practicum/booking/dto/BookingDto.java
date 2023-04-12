package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.booking.constants.BookingStatus;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Integer id;
    private Integer itemId;
    @NotNull(message = "The start of the ru.practicum.booking time is equal null")
    @FutureOrPresent(message = "The start of the ru.practicum.booking time is before present time")
    private LocalDateTime start;
    @NotNull(message = "The end of the ru.practicum.booking time is equal null")
    @FutureOrPresent(message = "The end of the ru.practicum.booking time is before present time")
    private LocalDateTime end;
    private Integer booker;
    private BookingStatus status;
}
