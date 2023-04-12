package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.constants.BookingStatus;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.repository.CommentsRepository;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.service.ItemService;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class ItemServiceImplTest {
    private final ItemService service;
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final EntityManager entityManager;
    private final ItemMapper mapper;
    private final UserMapper userMapper;
    private Item item;
    private ItemDto itemDto;
    private UserDto userDto;
    private User user;
    private User booker;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        user = userRepository.save(userToSave);
        userDto = userMapper.toDto(user);

        Item itemToSave = makeItem("Item1", "For something great", true, user);
        item = repository.save(itemToSave);
        itemDto = mapper.toDto(item);

        User bookerToSave = makeUser("James", "mail@mymail.com");
        booker = userRepository.save(bookerToSave);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void createItemWithoutRequestTest() {
        repository.deleteAll();

        assertThat(repository.findAll(), empty());

        Item item = makeItem("Item1", "For something", true, user);
        ItemDto itemDto = mapper.toDto(item);

        ItemDto itemFromRepository = service.createItem(user.getId(), itemDto);

        assertThat(itemFromRepository.getId(), notNullValue());
        assertThat(itemFromRepository.getRequestId(), nullValue());
        assertThat(item.getName(), is(itemFromRepository.getName()));
        assertThat(item.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(item.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void createItemWithRequestTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemRequest requestToSave = ItemRequest.builder().description("I need a hammer").requestor(booker).build();
        ItemRequest request = requestRepository.save(requestToSave);
        Item item = makeItem("Item2", "For something great", true, user);
        item.setRequest(request);
        ItemDto itemDto = mapper.toDto(item);

        ItemDto itemFromRepository = service.createItem(user.getId(), itemDto);

        assertThat(itemFromRepository.getId(), notNullValue());
        assertThat(itemFromRepository.getRequestId(), notNullValue());
        assertThat(itemFromRepository.getRequestId(), is(request.getId()));
        assertThat(item.getName(), is(itemFromRepository.getName()));
        assertThat(item.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(item.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void updateItemNameTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemDto itemWithNewName = ItemDto.builder().name("Item2").build();

        ItemDto itemFromRepository = service.updateItem(userDto.getId(), itemDto.getId(), itemWithNewName);

        assertThat(itemDto.getId(), is(itemFromRepository.getId()));
        assertThat(itemWithNewName.getName(), is(itemFromRepository.getName()));
        assertThat(itemDto.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(itemDto.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void updateItemDescriptionTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemDto itemWithNewDescription = ItemDto.builder().description("For something new").build();

        ItemDto itemFromRepository = service.updateItem(userDto.getId(), itemDto.getId(), itemWithNewDescription);

        assertThat(itemDto.getId(), is(itemFromRepository.getId()));
        assertThat(itemDto.getName(), is(itemFromRepository.getName()));
        assertThat(itemWithNewDescription.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(itemDto.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void updateItemAvailableTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemDto itemWithNewAvailability = ItemDto.builder().available(false).build();

        ItemDto itemFromRepository = service.updateItem(userDto.getId(), itemDto.getId(), itemWithNewAvailability);

        assertThat(itemDto.getId(), is(itemFromRepository.getId()));
        assertThat(itemDto.getName(), is(itemFromRepository.getName()));
        assertThat(itemDto.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(itemWithNewAvailability.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void updateItemTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemDto newItem = ItemDto.builder().name("New Item").description("Something new").available(false).build();

        ItemDto itemFromRepository = service.updateItem(userDto.getId(), itemDto.getId(), newItem);

        assertThat(itemDto.getId(), is(itemFromRepository.getId()));
        assertThat(newItem.getName(), is(itemFromRepository.getName()));
        assertThat(newItem.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(newItem.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void findItemByOwnerTest() {
        assertThat(repository.findAll(), notNullValue());

        Booking booking = makeBooking(item, booker);
        bookingRepository.save(booking);

        ItemInfo itemInfo = service.findItem(userDto.getId(), itemDto.getId());

        assertThat(itemDto.getId(), is(itemInfo.getId()));
        assertThat(itemDto.getName(), is(itemInfo.getName()));
        assertThat(itemDto.getDescription(), is(itemInfo.getDescription()));
        assertThat(itemDto.getAvailable(), is(itemInfo.getAvailable()));
        assertThat(itemInfo.getLastBooking(), nullValue());
        assertThat(itemInfo.getNextBooking(), notNullValue());
    }

    @Test
    void findAllItemsByUserTest() {
        Integer expectedSize = 1;

        List<ItemInfo> items = service.findAllItemsByUser(userDto.getId(), PageRequest.of(0,10));

        assertThat(items.size(), is(expectedSize));
        assertThat(itemDto.getId(), is(items.get(0).getId()));
        assertThat(itemDto.getDescription(), is(items.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), is(items.get(0).getAvailable()));
        assertThat(items.get(0).getComments(), empty());
        assertThat(items.get(0).getLastBooking(), nullValue());
        assertThat(items.get(0).getNextBooking(), nullValue());
    }

    @Test
    void findAllItemsByUserWithBookingTest() {
        Integer expectedSize = 1;

        Booking booking = makeBooking(item, booker);
        bookingRepository.save(booking);

        List<ItemInfo> items = service.findAllItemsByUser(userDto.getId(), PageRequest.of(0,10));

        assertThat(items.size(), is(expectedSize));
        assertThat(itemDto.getId(), is(items.get(0).getId()));
        assertThat(itemDto.getDescription(), is(items.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), is(items.get(0).getAvailable()));
        assertThat(items.get(0).getComments(), empty());
        assertThat(items.get(0).getLastBooking(), nullValue());
        assertThat(items.get(0).getNextBooking(), notNullValue());
        assertThat(booking.getBooker().getId(), is(items.get(0).getNextBooking().getBookerId()));
    }

    @Test
    void findAllItemsByUserWithBookingAndCommentTest() {
        Integer expectedSize = 1;

        Booking booking = makeBooking(item, booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
        CommentDto commentDto = makeCommentDto("Thanks a lot", booker.getName());
        service.addComment(booker.getId(), item.getId(), commentDto);

        List<ItemInfo> items = service.findAllItemsByUser(userDto.getId(), PageRequest.of(0,10));

        assertThat(items.size(), is(expectedSize));
        assertThat(itemDto.getId(), is(items.get(0).getId()));
        assertThat(itemDto.getDescription(), is(items.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), is(items.get(0).getAvailable()));
        assertThat(items.get(0).getComments(), notNullValue());
        assertThat(items.get(0).getComments().get(0).getAuthorName(), is(commentDto.getAuthorName()));
        assertThat(items.get(0).getLastBooking(), notNullValue());
        assertThat(items.get(0).getLastBooking().getBookerId(), is(items.get(0).getLastBooking().getBookerId()));
        assertThat(items.get(0).getNextBooking(), nullValue());
    }

    @Test
    void findItemByBookerTest() {
        assertThat(repository.findAll(), notNullValue());

        Booking booking = makeBooking(item, booker);
        bookingRepository.save(booking);

        ItemInfo itemInfo = service.findItem(booker.getId(), itemDto.getId());

        assertThat(itemDto.getId(), is(itemInfo.getId()));
        assertThat(itemDto.getName(), is(itemInfo.getName()));
        assertThat(itemDto.getDescription(), is(itemInfo.getDescription()));
        assertThat(itemDto.getAvailable(), is(itemInfo.getAvailable()));
        assertThat(itemInfo.getLastBooking(), nullValue());
        assertThat(itemInfo.getNextBooking(), nullValue());
    }

    @Test
    void searchItemForRentTest() {
        assertThat(repository.findAll(), notNullValue());

        List<ItemDto> itemDtoList = service.searchItemForRent("gReaT", PageRequest.of(0,10));

        assertThat(itemDto.getId(), is(itemDtoList.get(0).getId()));
        assertThat(itemDto.getName(), is(itemDtoList.get(0).getName()));
        assertThat(itemDto.getDescription(), is(itemDtoList.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), is(itemDtoList.get(0).getAvailable()));
    }

    @Test
    void searchItemForRentWithEmptyInputTest() {
        assertThat(repository.findAll(), notNullValue());

        List<ItemDto> itemDtoList = service.searchItemForRent("", PageRequest.of(0,10));

        assertThat(itemDtoList, empty());
    }

    @Test
    void addCommentTest() {
        assertThat(commentsRepository.findAll(), empty());

        Booking booking = makeBooking(item, booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
        CommentDto commentDto = makeCommentDto("Thanks a lot", booker.getName());

        CommentDto commentFromRepository = service.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(commentFromRepository.getId(), notNullValue());
        assertThat(commentDto.getText(), is(commentFromRepository.getText()));
        assertThat(commentDto.getAuthorName(), is(commentFromRepository.getAuthorName()));
    }

    private CommentDto makeCommentDto(String text, String authorName) {
        CommentDto.CommentDtoBuilder builder = CommentDto.builder();

        builder.text(text);
        builder.authorName(authorName);

        return builder.build();
    }

    private Booking makeBooking(Item item, User booker) {
        Booking.BookingBuilder builder = Booking.builder();

        builder.item(item);
        builder.start(LocalDateTime.of(2034, 1, 1, 11, 30));
        builder.end(LocalDateTime.of(2034, 1, 1, 12, 30));
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
        entityManager.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
