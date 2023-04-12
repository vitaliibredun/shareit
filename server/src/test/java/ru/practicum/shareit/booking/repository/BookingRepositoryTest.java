package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.dto.BookingFromRepository;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {
    private final TestEntityManager entityManager;
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user1 = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        user2 = User
                .builder()
                .name("James")
                .email("mymail@email.com")
                .build();

        user3 = User
                .builder()
                .name("John")
                .email("the@mail.com")
                .build();

        itemRequest = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(itemRequest)
                .build();

        item2 = Item
                .builder()
                .name("Item2")
                .description("Something else")
                .available(true)
                .owner(user2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        requestRepository.save(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyRepositoryIsEmpty() {
        List<Booking> bookings = repository.findAll();

        assertThat(bookings, empty());
    }

    @Test
    void verifyPersistingOfBooking() {
        Booking booking = Booking
                .builder()
                .item(item1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        assertThat(entityManager.getId(booking), nullValue());
        entityManager.persist(booking);
        assertThat(entityManager.getId(booking), notNullValue());
    }

    @Test
    void verifyBookingSaveInRepository() {
        Booking booking = Booking
                .builder()
                .item(item1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        assertThat(entityManager.getId(booking), nullValue());
        repository.save(booking);
        assertThat(entityManager.getId(booking), notNullValue());
    }

    @Test
    void verifyItemIsSameEntity() {
        Booking booking = Booking
                .builder()
                .item(item1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        assertThat(entityManager.getId(booking), nullValue());
        Booking bookingFromRepository = repository.save(booking);
        assertThat(entityManager.getId(booking), notNullValue());
        assertThat(booking.getItem(), sameInstance(bookingFromRepository.getItem()));
        assertThat(booking.getBooker(), sameInstance(bookingFromRepository.getBooker()));
        assertThat(booking.getStart(), sameInstance(bookingFromRepository.getStart()));
    }

    @Test
    void verifyRepositorySaveSameQuantityOfEntities() {
        Integer expectedSize = 2;

        Booking booking1 = Booking
                .builder()
                .item(item1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = Booking
                .builder()
                .item(item2)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(booking1), nullValue());
        assertThat(entityManager.getId(booking2), nullValue());
        repository.save(booking1);
        repository.save(booking2);
        assertThat(entityManager.getId(booking1), notNullValue());
        assertThat(entityManager.getId(booking2), notNullValue());
        assertThat(repository.findAll().size(), equalTo(expectedSize));
    }

    @Test
    void verifyFindAllByBooker() {
        Integer expectedSize = 1;

        Booking booking1 = Booking
                .builder()
                .item(item1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = Booking
                .builder()
                .item(item2)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .booker(user1)
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking3 = Booking
                .builder()
                .item(item2)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .booker(user3)
                .status(BookingStatus.WAITING)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(booking1), nullValue());
        assertThat(entityManager.getId(booking2), nullValue());
        assertThat(entityManager.getId(booking3), nullValue());
        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking3);
        assertThat(entityManager.getId(booking1), notNullValue());
        assertThat(entityManager.getId(booking2), notNullValue());
        assertThat(entityManager.getId(booking3), notNullValue());
        List<BookingFromRepository> bookingsFromRepository = repository.findAllByBooker(user1.getId(), PageRequest.of(0, 10));
        assertThat(bookingsFromRepository.size(), equalTo(expectedSize));
        assertThat(user1.getId(), sameInstance(booking2.getBooker().getId()));
        assertThat(booking2.getBooker().getId(), sameInstance(bookingsFromRepository.get(0).getBookerId()));
        assertThat(booking2.getItem().getId(), sameInstance(bookingsFromRepository.get(0).getItemId()));
    }

    private void resetIdColumns() {
        entityManager.getEntityManager()
                .createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
