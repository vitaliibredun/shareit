package ru.practicum.shareit.exceptions;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.controller.ErrorHandler;
import ru.practicum.shareit.exceptions.model.ErrorResponse;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorHandlerTest {
    private final ErrorHandler handler;

    @Test
    void verifyWrongOwnerOfItemException() {
        WrongOwnerOfItemException exception = new WrongOwnerOfItemException("Exception");
        ErrorResponse errorResponse = handler.handleWrongOwnerOfItem(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyEmailAlreadyExistException() {
        EmailAlreadyExistException exception = new EmailAlreadyExistException("Exception");
        ErrorResponse errorResponse = handler.handleEmailAlreadyExistException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("Exception");
        ErrorResponse errorResponse = handler.handleUserNotFoundException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyValidationException() {
        ValidationException exception = new ValidationException("Exception");
        ErrorResponse errorResponse = handler.handleValidationException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyWrongCustomerOfBookingException() {
        WrongCustomerOfBookingException exception = new WrongCustomerOfBookingException("Exception");
        ErrorResponse errorResponse = handler.handleWrongCustomerOfBookingException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyItemNotFoundException() {
        ItemNotFoundException exception = new ItemNotFoundException("Exception");
        ErrorResponse errorResponse = handler.handleItemNotFoundException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyBookingNotFoundException() {
        BookingNotFoundException exception = new BookingNotFoundException("Exception");
        ErrorResponse errorResponse = handler.handleBookingNotFoundException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyBookerIsOwnerOfItemException() {
        BookerIsOwnerOfItemException exception = new BookerIsOwnerOfItemException("Exception");
        ErrorResponse errorResponse = handler.handleBookerIsOwnerOfItemException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyRequestNotFoundException() {
        RequestNotFoundException exception = new RequestNotFoundException("Exception");
        ErrorResponse errorResponse = handler.handleRequestNotFoundException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }

    @Test
    void verifyException() {
        Exception exception = new Exception("Exception");
        ErrorResponse errorResponse = handler.handleException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }
}