package ru.practicum.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInfo;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.validation.BookingValidation;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;
import ru.practicum.user.validation.UserValidation;

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
    @CachePut(cacheNames = {"approvingBooking"}, key = "{#userId, #bookingId}")
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
    @Cacheable(cacheNames = {"findBooking"}, key = "{#userId, #bookingId}")
    public BookingInfo findBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingValidation.checkBookingData(userId, bookingId);
        return mapper.toDto(booking);
    }

    @Override
    @Cacheable(cacheNames = {"findAllBookingsCustomer"}, key = "{#userId, #state, #pageable.pageNumber, #pageable.pageSize}")
    public List<BookingInfo> findAllBookingsCustomer(Integer userId, String state, Pageable pageable) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByBooker(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    @Cacheable(cacheNames = {"findAllBookingsOwner"}, key = "{#userId, #state, #pageable.pageNumber, #pageable.pageSize}")
    public List<BookingInfo> findAllBookingsOwner(Integer userId, String state, Pageable pageable) {
        userValidation.checkUserExist(userId);
        switch (state) {
            case "CURRENT":
                return repository.findAllCurrentByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllPastByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllFutureByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllWaitingStatusByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return repository.findAllRejectedStatusByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "ALL":
                return repository.findAllByOwner(userId, pageable)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
