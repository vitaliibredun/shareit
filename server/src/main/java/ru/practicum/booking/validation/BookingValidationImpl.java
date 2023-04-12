package ru.practicum.booking.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exceptions.*;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;

import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class BookingValidationImpl implements BookingValidation {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking checkBookingData(Integer userId, Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.error("Validation failed. The ru.practicum.booking with the id {} doesn't exist", bookingId);
            throw new BookingNotFoundException("The ru.practicum.booking with the id doesn't exist");
        }
        Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
        if (item.isPresent()) {
            boolean customerOfBooking = booking.get().getBooker().getId().equals(userId);
            boolean ownerOfItem = item.get().getOwner().getId().equals(userId);
            if (!customerOfBooking && !ownerOfItem) {
                log.error("Validation failed. The wrong customer with id {} of ru.practicum.booking id {}", userId, bookingId);
                throw new WrongCustomerOfBookingException("The wrong customer of ru.practicum.booking");
            }
        }
        return booking.get();
    }

    @Override
    public Item checkItemData(User user, BookingDto bookingDto) {
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.error("Validation failed. The start of the ru.practicum.booking time {} is equal to the end of the ru.practicum.booking time {}",
                    bookingDto.getStart(), bookingDto.getEnd());
            throw new ValidationException("The start of the ru.practicum.booking time is equal to the end of the ru.practicum.booking time");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.error("Validation failed. The end of the ru.practicum.booking time {} is before the start time {}",
                    bookingDto.getEnd(), bookingDto.getStart());
            throw new ValidationException("The end of the ru.practicum.booking time is before the start time");
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            log.error("Validation failed. The ru.practicum.item with the id {} doesn't exists", bookingDto.getItemId());
            throw new ItemNotFoundException("The ru.practicum.item with the id doesn't exists");
        }
        boolean isAvailable = item.get().getAvailable().equals(true);
        if (!isAvailable) {
            log.error("Validation failed. The ru.practicum.item with id {} isn't available", bookingDto.getItemId());
            throw new ValidationException("The ru.practicum.item with id isn't available");
        }
        boolean ownerIsBooker = item.get().getOwner().getId().equals(user.getId());
        if (ownerIsBooker) {
            log.error("Validation failed. The booker is owner of the ru.practicum.booking ru.practicum.item");
            throw new BookerIsOwnerOfItemException("The booker is owner of the ru.practicum.booking ru.practicum.item");
        }
        return item.get();
    }

    @Override
    public Booking checkIfStatusAlreadyApproved(Integer userId, Integer bookingId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            boolean isOwnerOfItem = booking.get().getItem().getOwner().getId().equals(userId);
            if (!isOwnerOfItem) {
                log.error("Validation failed. The wrong owner with id {} of ru.practicum.item {}",
                        userId, booking.get().getItem().getName());
                throw new WrongOwnerOfItemException("The wrong owner of ru.practicum.item");
            }
        }
        if (booking.isEmpty()) {
            log.error("Validation failed. The ru.practicum.booking with the id doesn't exist");
            throw new BookingNotFoundException("The ru.practicum.booking with the id doesn't exist");
        }
        if (approved.equals(true)) {
            boolean statusAlreadyApproved = booking.get().getStatus().equals(BookingStatus.APPROVED);
            if (statusAlreadyApproved) {
                log.error("Validation failed. The status of ru.practicum.booking id {} is already approved", bookingId);
                throw new ValidationException("The status of ru.practicum.booking is already approved");
            }
        }
        return booking.get();
    }
}
