package ru.practicum.shareit.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.validation.CommentValidation;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentValidationImpl implements CommentValidation {
    private final BookingRepository bookingRepository;

    @Override
    public void checkUser(Integer userId, Integer itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            log.error("Validation failed. The text's comment is empty");
            throw new ValidationException("The text's comment is empty");
        }

        List<Optional<Booking>> bookings = bookingRepository.findByBookerAndItem(userId, itemId);
        if (bookings.isEmpty()) {
            log.error("Validation failed. The status of the booking is rejected");
            throw new ValidationException("The status of the booking is rejected");
        }
    }
}