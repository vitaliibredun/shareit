package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;
import java.util.stream.Collectors;

@Service("bookingServiceImpl")
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingValidation bookingValidation;
    private final UserValidation userValidation;
    private final BookingMapper mapper;

    @Override
    public BookingInfo createBooking(Integer userId, BookingDto bookingDto) {
        User user = userValidation.checkUserExist(userId);
        Item item = bookingValidation.checkItemData(user, bookingDto);
        Booking booking = mapper.toModel(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingFromRepository = repository.save(booking);
        return mapper.toDto(bookingFromRepository);
    }

    @Override
    public BookingInfo approvingBooking(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = bookingValidation.checkIfStatusAlreadyApproved(userId, bookingId, approved);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            repository.saveAndFlush(booking);
        }
        if (!approved) {
            booking.setStatus(BookingStatus.REJECTED);
            repository.saveAndFlush(booking);
        }
        return mapper.toDto(booking);
    }

    @Override
    public BookingInfo findBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingValidation.checkBookingData(userId, bookingId);
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingInfo> findAllBookingsCustomer(Integer userId, String state, Integer from, Integer size) {
        bookingValidation.checkPaginationInput(from, size);
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByBooker(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingInfo> findAllBookingsOwner(Integer userId, String state, Integer from, Integer size) {
        bookingValidation.checkPaginationInput(from, size);
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByOwner(userId)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
