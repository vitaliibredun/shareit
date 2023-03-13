package ru.practicum.shareit.booking.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortInfo;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.constants.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.BookingValidation;
import ru.practicum.shareit.validation.ItemValidation;
import ru.practicum.shareit.validation.UserValidation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("bookingServiceImpl")
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;
    private final ItemValidation itemValidation;
    private final BookingValidation bookingValidation;
    private final UserValidation userValidation;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository, UserRepository userRepository, BookingMapper mapper,
                              @Qualifier("itemValidationRepository") ItemValidation itemValidation,
                              BookingValidation bookingValidation,
                              @Qualifier("userValidationRepository") UserValidation userValidation) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.itemValidation = itemValidation;
        this.bookingValidation = bookingValidation;
        this.userValidation = userValidation;
    }

    @Override
    public BookingShortInfo createBooking(Integer userId, BookingDto bookingDto) {
        bookingValidation.checkIfCustomerIsOwnerOfBooking(userId, bookingDto);
        bookingValidation.checkIfItemAvailable(bookingDto.getItemId());
        bookingValidation.checkTimeBooking(bookingDto);
        userValidation.checkUserExist(userId);
        itemValidation.checkIfItemExist(bookingDto.getItemId());
        Booking bookingToRepository = mapper.toModel(bookingDto);
        bookingToRepository.setBooker(userId);
        bookingToRepository.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(bookingToRepository);
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
        User user = userRepository.findById(booking.getBooker()).orElseThrow();
        return mapper.toDtoInfo(booking, item, user);
    }

    @Override
    public BookingShortInfo approvingBooking(Integer userId, Integer bookingId, Boolean approved) {
        bookingValidation.checkIfStatusAlreadyApproved(userId, bookingId, approved);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            itemValidation.checkOwnerOfItem(userId, booking.getItemId());
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
                bookingRepository.saveAndFlush(booking);
            }
            if (!approved) {
                booking.setStatus(BookingStatus.REJECTED);
                bookingRepository.saveAndFlush(booking);
            }
            Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
            User user = userRepository.findById(booking.getBooker()).orElseThrow();
            return mapper.toDtoInfo(booking, item, user);
        }
        throw new BookingNotFoundException("The booking with the id doesn't exist");
    }

    @Override
    public BookingShortInfo findBooking(Integer userId, Integer bookingId) {
        bookingValidation.checkCustomerOfBooking(userId, bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
            User user = userRepository.findById(booking.getBooker()).orElseThrow();
            return mapper.toDtoInfo(booking, item, user);
        }
        throw new BookingNotFoundException("The booking with the id doesn't exist");
    }

    @Override
    public List<BookingShortInfo> findAllBookingsCustomer(Integer userId, String state) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllCurrentByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllPastByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllFutureByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllWaitingStatusByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllRejectedStatusByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findAllByBooker(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingShortInfo> findAllBookingsOwner(Integer userId, String state) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllCurrentByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllPastByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllFutureByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllWaitingStatusByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllRejectedStatusByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findAllByOwner(userId)
                        .stream()
                        .map(mapper::toDtoInfoFromBookingInfo)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
