package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingInfo createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @Valid @RequestBody BookingDto bookingDto) {
        return service.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfo approvingBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @PathVariable Integer bookingId,
                                        @RequestParam("approved") Boolean approved) {
        return service.approvingBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfo findBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @PathVariable Integer bookingId) {
        return service.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfo> findAllBookingsCustomer(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                                     @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return service.findAllBookingsCustomer(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfo> findAllBookingsOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return service.findAllBookingsOwner(userId, state, from, size);
    }
}
