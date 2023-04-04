package ru.practicum.shareit.exceptions.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.exceptions.model.ErrorResponse;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {
        log.warn("500 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongOwnerOfItem(final WrongOwnerOfItemException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistException(final EmailAlreadyExistException exception) {
        log.warn("409 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException exception) {
        log.warn("400 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongCustomerOfBookingException(final WrongCustomerOfBookingException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookerIsOwnerOfItemException(final BookerIsOwnerOfItemException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestNotFoundException(final RequestNotFoundException exception) {
        log.warn("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
