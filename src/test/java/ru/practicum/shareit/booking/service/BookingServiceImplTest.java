package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class BookingServiceImplTest {
    private final BookingService service;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository repository;
    private final EntityManager entityManager;
    private final BookingMapper mapper;
    private final UserMapper userMapper;
    private BookingInfo bookingInfo1;
    private UserDto userDto;
    private Item item;
    private User booker;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        User user = userRepository.save(userToSave);
        userDto = userMapper.toDto(user);

        Item itemToSave = makeItem("Item1", "For something great", true, user);
        item = itemRepository.save(itemToSave);

        User bookerToSave = makeUser("James", "mail@mymail.com");
        booker = userRepository.save(bookerToSave);

        Booking bookingToSave = makeBooking(item, booker, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        Booking booking = repository.save(bookingToSave);
        bookingInfo1 = mapper.toDto(booking);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDto = makeBookingDto(item.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), booker.getId());

        BookingInfo bookingFromRepository = service.createBooking(booker.getId(), bookingDto);

        assertThat(bookingFromRepository.getId(), notNullValue());
        assertThat(bookingDto.getStart(), is(bookingFromRepository.getStart()));
        assertThat(bookingDto.getItemId(), is(bookingFromRepository.getItem().getId()));
        assertThat(bookingDto.getBooker(), is(bookingFromRepository.getBooker().getId()));
    }

    @Test
    void approvingBookingTest() {
        assertThat(repository.findAll(), notNullValue());
        assertThat(bookingInfo1.getStatus(), is(BookingStatus.WAITING));

        BookingInfo bookingFromRepository = service
                .approvingBooking(userDto.getId(), bookingInfo1.getId(), true);

        assertThat(bookingInfo1.getId(), is(bookingFromRepository.getId()));
        assertThat(bookingInfo1.getItem(), is(bookingFromRepository.getItem()));
        assertThat(bookingInfo1.getBooker(), is(bookingFromRepository.getBooker()));
        assertThat(bookingFromRepository.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void rejectingBookingTest() {
        assertThat(repository.findAll(), notNullValue());
        assertThat(bookingInfo1.getStatus(), is(BookingStatus.WAITING));

        BookingInfo bookingFromRepository = service
                .approvingBooking(userDto.getId(), bookingInfo1.getId(), false);

        assertThat(bookingInfo1.getId(), is(bookingFromRepository.getId()));
        assertThat(bookingInfo1.getItem(), is(bookingFromRepository.getItem()));
        assertThat(bookingInfo1.getBooker(), is(bookingFromRepository.getBooker()));
        assertThat(bookingFromRepository.getStatus(), is(BookingStatus.REJECTED));
    }

    @Test
    void findBookingTest() {
        assertThat(repository.findAll(), notNullValue());

        BookingInfo bookingFromRepository = service.findBooking(userDto.getId(), bookingInfo1.getId());

        assertThat(bookingInfo1.getId(), is(bookingFromRepository.getId()));
        assertThat(bookingInfo1.getItem(), is(bookingFromRepository.getItem()));
        assertThat(bookingInfo1.getBooker(), is(bookingFromRepository.getBooker()));
        assertThat(bookingInfo1.getStatus(), is(bookingFromRepository.getStatus()));
        assertThat(bookingInfo1.getStart(), is(bookingFromRepository.getStart()));
        assertThat(bookingInfo1.getEnd(), is(bookingFromRepository.getEnd()));
    }

    @Test
    void findAllBookingsCustomerCurrentTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 1;

        assertThat(repository.findAll().size(), is(currentSize));

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "CURRENT", 0, 10);

        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsCustomerPastTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item, booker, LocalDateTime.now(), LocalDateTime.now().plusNanos(10));
        Booking booking = repository.save(bookingToSave);
        bookingInfo1 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "PAST", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsCustomerFutureTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        bookingInfo1 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "FUTURE", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsCustomerWaitingTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 2;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        BookingInfo bookingInfo2 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "WAITING", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo2.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo2.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo2.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(1).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(1).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(1).getBooker().getId()));
    }

    @Test
    void findAllBookingsCustomerRejectedTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        repository.save(bookingToSave);
        BookingInfo bookingFromRepository = service.approvingBooking(userDto.getId(), bookingInfo1.getId(), false);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "REJECTED", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo1.getId(), is(bookingFromRepository.getId()));
        assertThat(bookingFromRepository.getStatus(), is(BookingStatus.REJECTED));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsCustomerTest() {
        Integer currentSize = 1;
        Integer expectedSizeToCustomer = 2;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        BookingInfo bookingInfo2 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsCustomer(booker.getId(), "ALL", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToCustomer));
        assertThat(bookingInfo2.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo2.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo2.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(1).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(1).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(1).getBooker().getId()));
    }

    @Test
    void verifyFindAllBookingsCustomerException() {
        assertThat(repository.findAll(), notNullValue());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllBookingsCustomer(booker.getId(), "MINE", 0, 10));

        assertThat("Unknown state: UNSUPPORTED_STATUS", is(exception.getMessage()));
    }

    @Test
    void findAllBookingsOwnerCurrentTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 1;

        assertThat(repository.findAll().size(), is(currentSize));

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "CURRENT", 0, 10);

        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsOwnerPastTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item, booker, LocalDateTime.now(), LocalDateTime.now().plusNanos(10));
        Booking booking = repository.save(bookingToSave);
        bookingInfo1 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "PAST", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsOwnerFutureTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        bookingInfo1 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "FUTURE", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsOwnerWaitingTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 2;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        BookingInfo bookingInfo2 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "WAITING", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo2.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo2.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo2.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(1).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(1).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(1).getBooker().getId()));
    }

    @Test
    void findAllBookingsOwnerRejectedTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 1;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        repository.save(bookingToSave);
        BookingInfo bookingFromRepository = service.approvingBooking(userDto.getId(), bookingInfo1.getId(), false);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "REJECTED", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo1.getId(), is(bookingFromRepository.getId()));
        assertThat(bookingFromRepository.getStatus(), is(BookingStatus.REJECTED));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
    }

    @Test
    void findAllBookingsOwnerTest() {
        Integer currentSize = 1;
        Integer expectedSizeToOwner = 2;
        Integer allBookingsSize = 2;

        assertThat(repository.findAll().size(), is(currentSize));

        Booking bookingToSave = makeBooking(item,
                booker, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        Booking booking = repository.save(bookingToSave);
        BookingInfo bookingInfo2 = mapper.toDto(booking);

        List<BookingInfo> bookingsCustomer = service
                .findAllBookingsOwner(userDto.getId(), "ALL", 0, 10);

        assertThat(repository.findAll().size(), is(allBookingsSize));
        assertThat(bookingsCustomer.size(), is(expectedSizeToOwner));
        assertThat(bookingInfo2.getId(), is(bookingsCustomer.get(0).getId()));
        assertThat(bookingInfo2.getItem().getId(), is(bookingsCustomer.get(0).getItem().getId()));
        assertThat(bookingInfo2.getBooker().getId(), is(bookingsCustomer.get(0).getBooker().getId()));
        assertThat(bookingInfo1.getId(), is(bookingsCustomer.get(1).getId()));
        assertThat(bookingInfo1.getItem().getId(), is(bookingsCustomer.get(1).getItem().getId()));
        assertThat(bookingInfo1.getBooker().getId(), is(bookingsCustomer.get(1).getBooker().getId()));
    }

    @Test
    void verifyFindAllBookingsOwnerException() {
        assertThat(repository.findAll(), notNullValue());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllBookingsOwner(userDto.getId(), "MINE", 0, 10));

        assertThat("Unknown state: UNSUPPORTED_STATUS", is(exception.getMessage()));
    }

    private BookingDto makeBookingDto(Integer itemId, LocalDateTime start, LocalDateTime end,Integer booker) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.itemId(itemId);
        builder.start(start);
        builder.end(end);
        builder.booker(booker);
        builder.status(BookingStatus.WAITING);

        return builder.build();
    }

    private Booking makeBooking(Item item, User booker, LocalDateTime start, LocalDateTime end) {
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

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
