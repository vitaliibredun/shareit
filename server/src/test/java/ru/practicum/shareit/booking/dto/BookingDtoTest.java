package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.dto.*;
import ru.practicum.item.model.Item;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> bookingDtoTester;
    @Autowired
    private JacksonTester<Booker> bookerTester;
    @Autowired
    private JacksonTester<BookingFromRepository> bookingFromRepositoryTester;
    @Autowired
    private JacksonTester<BookingInfo> bookingInfoTester;
    @Autowired
    private JacksonTester<LastBooking> lastBookingTester;
    @Autowired
    private JacksonTester<NextBooking> nextBookingTester;

    @Test
    void verifyCreateBookingDto() throws IOException {
        LocalDateTime start = LocalDateTime.of(2034, 4, 1, 11, 30);
        LocalDateTime end = LocalDateTime.of(2034, 4, 1, 12, 30);

        BookingDto bookingDto = makeBookingDto(1,
                1, start, end, 1, BookingStatus.WAITING);

        JsonContent<BookingDto> result = bookingDtoTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2034-04-01T11:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2034-04-01T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void verifyCreateBooker() throws IOException {
        Booker booker = makeBooker(1, "Timmy");

        JsonContent<Booker> result = bookerTester.write(booker);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Timmy");
    }

    @Test
    void verifyCreateBookingFromRepository() throws IOException {
        LocalDateTime start = LocalDateTime.of(2034, 4, 1, 11, 30);
        LocalDateTime end = LocalDateTime.of(2034, 4, 1, 12, 30);

        BookingFromRepository bookingFromRepository = makeBookingFromRepository(1,
                1, "Item1", start, end, 2, "Bond", BookingStatus.APPROVED);

        JsonContent<BookingFromRepository> result = bookingFromRepositoryTester.write(bookingFromRepository);

        assertThat(result).extractingJsonPathNumberValue("$.bookingId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2034-04-01T11:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2034-04-01T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.bookerName").isEqualTo("Bond");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void verifyCreateBookingInfo() throws IOException {
        LocalDateTime start = LocalDateTime.of(2034, 4, 1, 11, 30);
        LocalDateTime end = LocalDateTime.of(2034, 4, 1, 12, 30);
        Item item = Item.builder().build();
        Booker booker = Booker.builder().build();

        BookingInfo bookingInfo = makeBookingInfo(1, item, start, end, booker, BookingStatus.REJECTED);

        JsonContent<BookingInfo> result = bookingInfoTester.write(bookingInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.ru.practicum.item.id").isEqualTo(item.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2034-04-01T11:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2034-04-01T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(booker.getId());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");
    }

    @Test
    void verifyCreateLastBooking() throws IOException {
        LocalDateTime time = LocalDateTime.of(2034, 4, 1, 11, 30);

        LastBooking lastBooking = makeLastBooking(1, 1, time);

        JsonContent<LastBooking> result = lastBookingTester.write(lastBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.time").isEqualTo("2034-04-01T11:30:00");
    }

    @Test
    void verifyCreateNextBooking() throws IOException {
        LocalDateTime time = LocalDateTime.of(2034, 4, 1, 11, 30);

        NextBooking nextBooking = makeNextBooking(1, 1, time);

        JsonContent<NextBooking> result = nextBookingTester.write(nextBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.time").isEqualTo("2034-04-01T11:30:00");
    }

    private NextBooking makeNextBooking(Integer id, Integer bookerId, LocalDateTime time) {
        NextBooking.NextBookingBuilder builder = NextBooking.builder();

        builder.id(id);
        builder.bookerId(bookerId);
        builder.time(time);

        return builder.build();
    }

    private LastBooking makeLastBooking(Integer id, Integer bookerId, LocalDateTime time) {
        LastBooking.LastBookingBuilder builder = LastBooking.builder();

        builder.id(id);
        builder.bookerId(bookerId);
        builder.time(time);

        return builder.build();
    }

    private BookingInfo makeBookingInfo(Integer id,
                                        Item item,
                                        LocalDateTime start,
                                        LocalDateTime end,
                                        Booker booker,
                                        BookingStatus status) {

        BookingInfo.BookingInfoBuilder builder = BookingInfo.builder();

        builder.id(id);
        builder.item(item);
        builder.start(start);
        builder.end(end);
        builder.booker(booker);
        builder.status(status);

        return builder.build();
    }

    private BookingFromRepository makeBookingFromRepository(Integer bookingId,
                                                            Integer itemId,
                                                            String name,
                                                            LocalDateTime start,
                                                            LocalDateTime end,
                                                            Integer bookerId,
                                                            String bookerName,
                                                            BookingStatus status) {

        BookingFromRepository.BookingFromRepositoryBuilder builder = BookingFromRepository.builder();

        builder.bookingId(bookingId);
        builder.itemId(itemId);
        builder.name(name);
        builder.start(start);
        builder.end(end);
        builder.bookerId(bookerId);
        builder.bookerName(bookerName);
        builder.status(status);

        return builder.build();
    }

    private Booker makeBooker(Integer id, String name) {
        Booker.BookerBuilder builder = Booker.builder();

        builder.id(id);
        builder.name(name);

        return builder.build();
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
