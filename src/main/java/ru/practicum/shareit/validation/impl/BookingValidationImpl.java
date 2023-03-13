package ru.practicum.shareit.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.constants.BookingStatus;
import ru.practicum.shareit.exceptions.BookerIsOwnerOfItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongCustomerOfBookingException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.BookingValidation;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class BookingValidationImpl implements BookingValidation {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public void checkCustomerOfBooking(Integer userId, Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Optional<Item> item = itemRepository.findById(booking.get().getItemId());
            if (item.isPresent()) {
                boolean customerOfBooking = booking.get().getBooker().equals(userId);
                boolean ownerOfItem = item.get().getOwner().equals(userId);
                if (!customerOfBooking && !ownerOfItem) {
                    log.error("Validation failed. The wrong customer with id {} of booking id {}", userId, bookingId);
                    throw new WrongCustomerOfBookingException("The wrong customer of booking");
                }
            }
        }
    }

    @Override
    public void checkIfItemAvailable(Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            boolean isAvailable = item.get().getAvailable().equals(true);
            if (!isAvailable) {
                log.error("Validation failed. The item with id {} isn't available", itemId);
                throw new ValidationException("The item with id isn't available");
            }
        }
    }

    @Override
    public void checkTimeBooking(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            log.error("Validation failed. The end of the booking time {} is before present time", bookingDto.getEnd());
            throw new ValidationException("The end of the booking time is before present time");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.error("Validation failed. The end of the booking time {} is before the start time {}", bookingDto.getEnd(), bookingDto.getStart());
            throw new ValidationException("The end of the booking time is before the start time");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            log.error("Validation failed. The start of the booking time {} is before present time", bookingDto.getStart());
            throw new ValidationException("The start of the booking time is before present time");
        }
    }

    @Override
    public void checkIfStatusAlreadyApproved(Integer userId, Integer bookingId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent() && approved.equals(true)) {
            boolean statusAlreadyApproved = booking.get().getStatus().equals(BookingStatus.APPROVED);
            if (statusAlreadyApproved) {
                log.error("Validation failed. The status of booking id {} is already approved", bookingId);
                throw new ValidationException("The status of booking is already approved");
            }
        }
    }

    @Override
    public void checkIfCustomerIsOwnerOfBooking(Integer userId, BookingDto bookingDto) {
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isPresent()) {
            boolean ownerIsBooker = item.get().getOwner().equals(userId);
            if (ownerIsBooker) {
                log.error("Validation failed. The booker is owner of the booking item");
                throw new BookerIsOwnerOfItemException("The booker is owner of the booking item");
            }
        }
    }
}
