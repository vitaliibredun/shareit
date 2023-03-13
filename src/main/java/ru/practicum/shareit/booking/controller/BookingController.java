package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortInfo;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingShortInfo createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingShortInfo approvingBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @PathVariable Integer bookingId,
                                       @RequestParam("approved") Boolean approved) {
        return bookingService.approvingBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingShortInfo findBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable Integer bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingShortInfo> findAllBookingsCustomer(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                    String state) {
        return bookingService.findAllBookingsCustomer(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingShortInfo> findAllBookingsOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                 String state) {
        return bookingService.findAllBookingsOwner(userId, state);
    }
}
