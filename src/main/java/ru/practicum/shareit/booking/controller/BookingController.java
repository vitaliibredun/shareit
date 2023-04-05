package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;

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
        if (from < 0) {
            throw new ValidationException("The parameter of page must  not be less than zero");
        }
        return service.findAllBookingsCustomer(userId, state, PageRequest.of(from / size, size, Sort.by("start").descending()));
    }

    @GetMapping("/owner")
    public List<BookingInfo> findAllBookingsOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        if (from < 0) {
            throw new ValidationException("The parameter of page must  not be less than zero");
        }
        return service.findAllBookingsOwner(userId, state, PageRequest.of(from / size, size, Sort.by("start").descending()));
    }
}
