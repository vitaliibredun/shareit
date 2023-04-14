package ru.practicum.comments.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.model.Booking;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentValidationImpl implements CommentValidation {
    private final BookingRepository bookingRepository;

    @Override
    public void checkUser(Integer userId, Integer itemId, CommentDto commentDto) {
        List<Optional<Booking>> bookings = bookingRepository.findByBookerAndItem(userId, itemId);
        if (bookings.isEmpty()) {
            log.error("Validation failed. The status of the ru.practicum.booking is rejected");
            throw new ValidationException("The status of the ru.practicum.booking is rejected");
        }
    }
}