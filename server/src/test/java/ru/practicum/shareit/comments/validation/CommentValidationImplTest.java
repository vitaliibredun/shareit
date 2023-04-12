package ru.practicum.shareit.comments.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.validation.CommentValidation;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class CommentValidationImplTest {
    private final CommentValidation validation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private User user;
    private Item item;
    private Booking booking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        user = userRepository.save(userToSave);

        Item itemToSave = makeItem("Item1", "For something", true, user);
        item = itemRepository.save(itemToSave);

        Booking bookingToSave = makeBooking(item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), user);
        booking = bookingRepository.save(bookingToSave);

        commentDto = makeCommentDto("I need some stuff", user.getName());
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyCheckUserException() {
        booking.setStatus(BookingStatus.REJECTED);

        final ValidationException exception = assertThrows(
                ValidationException.class, () -> validation.checkUser(user.getId(), item.getId(), commentDto));

        assertThat("The status of the ru.practicum.booking is rejected", is(exception.getMessage()));
    }

    private CommentDto makeCommentDto(String text, String authorName) {
        CommentDto.CommentDtoBuilder builder = CommentDto.builder();

        builder.text(text);
        builder.authorName(authorName);

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

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
