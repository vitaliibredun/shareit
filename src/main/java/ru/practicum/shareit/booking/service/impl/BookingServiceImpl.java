package ru.practicum.shareit.booking.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.constants.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;
import java.util.stream.Collectors;

@Service("bookingServiceImpl")
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingValidation bookingValidation;
    private final UserValidation userValidation;
    private final BookingMapper mapper;

    public BookingServiceImpl(BookingRepository repository,
                              BookingValidation bookingValidation,
                              @Qualifier("userValidationRepository") UserValidation userValidation,
                              BookingMapper mapper) {
        this.repository = repository;
        this.bookingValidation = bookingValidation;
        this.userValidation = userValidation;
        this.mapper = mapper;
    }

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
        Booking booking = bookingValidation.checkCustomerOfBooking(userId, bookingId);
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingInfo> findAllBookingsCustomer(Integer userId, String state) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByBooker(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingInfo> findAllBookingsOwner(Integer userId, String state) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByOwner(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
