package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void verifyCreateBookingDto() throws IOException {
        LocalDateTime start = LocalDateTime.of(2023, 4, 1, 11, 30);
        LocalDateTime end = LocalDateTime.of(2023, 4, 1, 12, 30);

        BookingDto bookingDto = makeBookingDto(1,
                1, start, end, 1, BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-04-01T11:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-04-01T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    private BookingDto makeBookingDto(Integer id,
                                      Integer itemId,
                                      LocalDateTime start,
                                      LocalDateTime end,
                                      Integer booker,
                                      BookingStatus status) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.id(id);
        builder.itemId(itemId);
        builder.start(start);
        builder.end(end);
        builder.booker(booker);
        builder.status(status);

        return builder.build();
    }
}
