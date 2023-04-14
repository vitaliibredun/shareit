package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.controller.BookingController;
import ru.practicum.booking.dto.Booker;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInfo;
import ru.practicum.booking.service.BookingService;
import ru.practicum.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private BookingDto bookingDto;
    private BookingInfo bookingInfo1;
    private BookingInfo bookingInfo2;
    private BookingInfo bookingInfo3;

    @BeforeEach
    void setUp() {
        bookingDto = makeBookingDto(1, 1, 30);

        bookingInfo1 = makeBookingInfo(1, 30);
        bookingInfo2 = makeBookingInfo(2, 40);
        bookingInfo3 = makeBookingInfo(3, 50);
    }

    @Test
    void createBooking() throws Exception {
        Integer userId = 1;

        when(service.createBooking(anyInt(), any(BookingDto.class)))
                .thenReturn(bookingInfo1);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker.name", is(bookingInfo1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingInfo1.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingInfo1.getStatus().toString())));
    }

    @Test
    void approvingBooking() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;
        bookingInfo1.setStatus(BookingStatus.APPROVED);

        when(service.approvingBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingInfo1);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(bookingInfo1.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(bookingInfo1.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingInfo1.getStatus().toString())));
    }

    @Test
    void findBooking() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        when(service.findBooking(anyInt(), anyInt()))
                .thenReturn(bookingInfo1);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(bookingInfo1.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(bookingInfo1.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingInfo1.getStatus().toString())));
    }

    @Test
    void findAllBookingsCustomer() throws Exception {
        Integer userId = 1;
        Integer expectedSize = 3;

        when(service.findAllBookingsCustomer(anyInt(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(bookingInfo1, bookingInfo2, bookingInfo3));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].item.name", is(bookingInfo1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingInfo1.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingInfo1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].item.name", is(bookingInfo2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.name", is(bookingInfo2.getBooker().getName())))
                .andExpect(jsonPath("$.[1].status", is(bookingInfo2.getStatus().toString())))
                .andExpect(jsonPath("$.[2].item.name", is(bookingInfo3.getItem().getName())))
                .andExpect(jsonPath("$.[2].booker.name", is(bookingInfo3.getBooker().getName())))
                .andExpect(jsonPath("$.[2].status", is(bookingInfo3.getStatus().toString())));
    }

    @Test
    void findAllBookingsOwner() throws Exception {
        Integer userId = 1;
        Integer expectedSize = 3;

        when(service.findAllBookingsOwner(anyInt(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(bookingInfo1, bookingInfo2, bookingInfo3));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].item.name", is(bookingInfo1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingInfo1.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingInfo1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].item.name", is(bookingInfo2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.name", is(bookingInfo2.getBooker().getName())))
                .andExpect(jsonPath("$.[1].status", is(bookingInfo2.getStatus().toString())))
                .andExpect(jsonPath("$.[2].item.name", is(bookingInfo3.getItem().getName())))
                .andExpect(jsonPath("$.[2].booker.name", is(bookingInfo3.getBooker().getName())))
                .andExpect(jsonPath("$.[2].status", is(bookingInfo3.getStatus().toString())));
    }

    private BookingInfo makeBookingInfo(Integer hour, Integer minutes) {
        BookingInfo.BookingInfoBuilder builder = BookingInfo.builder();

        builder.item(Item.builder().build());
        builder.start(LocalDateTime.now().plusHours(hour));
        builder.end(LocalDateTime.now().plusMinutes(minutes));
        builder.booker(Booker.builder().build());
        builder.status(BookingStatus.WAITING);

        return builder.build();
    }

    private BookingDto makeBookingDto(Integer itemId, Integer hour, Integer minutes) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.itemId(itemId);
        builder.start(LocalDateTime.now().plusHours(hour));
        builder.end(LocalDateTime.now().plusMinutes(minutes));

        return builder.build();
    }
}
