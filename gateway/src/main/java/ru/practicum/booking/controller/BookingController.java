package ru.practicum.booking.controller;

import ru.practicum.booking.client.BookingClient;
import ru.practicum.booking.dto.BookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating ru.practicum.booking {}, from userId={}", bookingDto, userId);
        return client.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvingBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @PathVariable Integer bookingId,
                                                   @RequestParam("approved") Boolean approved) {
        log.info("Approving bookingId={}, with {} status", bookingId, approved);
        return client.approvingBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer bookingId) {
        log.info("Get ru.practicum.booking with id={} to userId={}", bookingId, userId);
        return client.findBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsCustomer(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("Get bookings with state {} to userId={} from={} to size={}", state, userId, from, size);
        return client.findAllBookingsCustomer(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("Get bookings with state {} to owner with id={} from={} to size={}", state, userId, from, size);
        return client.findAllBookingsOwner(userId, state, from, size);
    }
}
