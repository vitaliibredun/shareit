package ru.practicum.shareit.booking.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.validation.BookingValidation;
import ru.practicum.exceptions.*;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class BookingValidationImplTest {
    private final BookingValidation validation;
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        user = userRepository.save(userToSave);

        Item itemToSave = makeItem("Item1", "For something", true, user);
        item = itemRepository.save(itemToSave);

        Booking bookingToSave = makeBooking(item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), user);
        booking = repository.save(bookingToSave);
    }

    @Test
    void verifyCheckBookingDataByBookingException() {
        assertThat(repository.findAll(), notNullValue());

        Booking someBooking = makeBooking(Item.builder().build(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                User.builder().build());
        someBooking.setId(100);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class, () -> validation.checkBookingData(user.getId(), someBooking.getId()));

        assertThat("The ru.practicum.booking with the id doesn't exist", is(exception.getMessage()));
    }

    @Test
    void verifyCheckBookingDataByCustomerException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("James", "mymail@email.com");
        User userWithoutBooking = userRepository.save(userToSave);
        Item itemToSave = makeItem("Item2", "For somebody", true, userWithoutBooking);
        itemRepository.save(itemToSave);

        final WrongCustomerOfBookingException exception = assertThrows(
                WrongCustomerOfBookingException.class,
                () -> validation.checkBookingData(userWithoutBooking.getId(), booking.getId()));

        assertThat("The wrong customer of ru.practicum.booking", is(exception.getMessage()));
    }

    @Test
    void verifyCheckItemDataEqualTimeException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Bond", "mymail@mail.com");
        User newUser = userRepository.save(userToSave);
        Booking newBooking = makeBooking(item,
                LocalDateTime.of(2023, 4, 1, 11, 30),
                LocalDateTime.of(2023, 4, 1, 11, 30),
                newUser);
        BookingDto bookingDto = makeBookingDto(newBooking);

        final ValidationException exception = assertThrows(
                ValidationException.class, () -> validation.checkItemData(newUser, bookingDto));

        assertThat("The start of the ru.practicum.booking time is equal to the end of the ru.practicum.booking time",
                is(exception.getMessage()));
    }

    @Test
    void verifyCheckItemDataEndTimeException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Bond", "mymail@mail.com");
        User newUser = userRepository.save(userToSave);
        Booking newBooking = makeBooking(item,
                LocalDateTime.of(2023, 4, 1, 11, 30),
                LocalDateTime.of(2023, 4, 1, 11, 0),
                newUser);
        BookingDto bookingDto = makeBookingDto(newBooking);

        final ValidationException exception = assertThrows(
                ValidationException.class, () -> validation.checkItemData(newUser, bookingDto));

        assertThat("The end of the ru.practicum.booking time is before the start time", is(exception.getMessage()));
    }

    @Test
    void verifyCheckItemDataItemException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Bond", "mymail@mail.com");
        User newUser = userRepository.save(userToSave);
        Item newItem = makeItem("Item3", "For something great", true, newUser);
        newItem.setId(100);
        Booking newBooking = makeBooking(newItem, LocalDateTime.now(), LocalDateTime.now().plusHours(1), newUser);
        BookingDto bookingDto = makeBookingDto(newBooking);

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class, () -> validation.checkItemData(newUser, bookingDto));

        assertThat("The ru.practicum.item with the id doesn't exists", is(exception.getMessage()));
    }

    @Test
    void verifyCheckItemDataAvailableException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Bond", "mymail@mail.com");
        User newUser = userRepository.save(userToSave);
        Item itemToSave = makeItem("Item3", "For something great", false, user);
        Item newItem = itemRepository.save(itemToSave);
        Booking newBooking = makeBooking(newItem, LocalDateTime.now(), LocalDateTime.now().plusHours(1), newUser);
        BookingDto bookingDto = makeBookingDto(newBooking);

        final ValidationException exception = assertThrows(
                ValidationException.class, () -> validation.checkItemData(newUser, bookingDto));

        assertThat("The ru.practicum.item with id isn't available", is(exception.getMessage()));
    }

    @Test
    void verifyCheckItemDataOwnerException() {
        assertThat(repository.findAll(), notNullValue());

        BookingDto bookingDto = makeBookingDto(booking);

        final BookerIsOwnerOfItemException exception = assertThrows(
                BookerIsOwnerOfItemException.class, () -> validation.checkItemData(user, bookingDto));

        assertThat("The booker is owner of the ru.practicum.booking ru.practicum.item", is(exception.getMessage()));
    }

    @Test
    void verifyCheckIfStatusAlreadyApprovedOwnerException() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Bond", "mymail@mail.com");
        User notAOwner = userRepository.save(userToSave);

        final WrongOwnerOfItemException exception = assertThrows(
                WrongOwnerOfItemException.class,
                () -> validation.checkIfStatusAlreadyApproved(notAOwner.getId(), booking.getId(), true));

        assertThat("The wrong owner of ru.practicum.item", is(exception.getMessage()));
    }

    @Test
    void verifyCheckIfStatusAlreadyApprovedBookingException() {
        assertThat(repository.findAll(), notNullValue());

        Booking newBooking = makeBooking(item, LocalDateTime.now(), LocalDateTime.now().plusHours(1), user);
        newBooking.setId(100);

        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> validation.checkIfStatusAlreadyApproved(user.getId(), newBooking.getId(), true));

        assertThat("The ru.practicum.booking with the id doesn't exist", is(exception.getMessage()));
    }

    @Test
    void verifyCheckIfStatusAlreadyApprovedAlreadyException() {
        assertThat(repository.findAll(), notNullValue());

        booking.setStatus(BookingStatus.APPROVED);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validation.checkIfStatusAlreadyApproved(user.getId(), booking.getId(), true));

        assertThat("The status of ru.practicum.booking is already approved", is(exception.getMessage()));
    }

    private BookingDto makeBookingDto(Booking booking) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.itemId(booking.getItem().getId());
        builder.start(booking.getStart());
        builder.end(booking.getEnd());
        builder.booker(booking.getBooker().getId());
        builder.status(booking.getStatus());

        return builder.build();
    }

    private Booking makeBooking(Item item, LocalDateTime start, LocalDateTime end,User booker) {
        Booking.BookingBuilder builder = Booking.builder();

        builder.item(item);
        builder.start(start);
        builder.end(end);
        builder.booker(booker);
        builder.status(BookingStatus.WAITING);

        return builder.build();
    }

    private Item makeItem(String name, String description, Boolean available, User owner) {
        Item.ItemBuilder builder = Item.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);
        builder.owner(owner);

        return builder.build();
    }

    private User makeUser(String name, String email) {
        User.UserBuilder builder = User.builder();

        builder.name(name);
        builder.email(email);

        return builder.build();
    }
}
