package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

        User userToSave = makeUser("Timmy", "mail@email.com");
        User user = userRepository.save(userToSave);
        Item itemToSave = makeItem("Item1", "For something", true, user);
        Item itemFromRepository = repository.save(itemToSave);

        assertThat(itemFromRepository.getId(), notNullValue());
        assertThat(itemFromRepository.getRequest(), nullValue());
        assertThat(itemToSave.getName(), is(itemFromRepository.getName()));
        assertThat(itemToSave.getDescription(), is(itemFromRepository.getDescription()));
        assertThat(itemToSave.getAvailable(), is(itemFromRepository.getAvailable()));
    }

    @Test
    void createItemWithRequestTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemRequest requestToSave = ItemRequest.builder().description("I need a hammer").requestor(booker).build();
        ItemRequest request = requestRepository.save(requestToSave);
        ItemDto itemDto = makeItemDto(request.getId(), "Hammer", "The best hammer", true);
        Item newItem = mapper.toModel(itemDto, request);
        newItem.setOwner(user);
        Item itemFromRepository = repository.save(newItem);

        assertThat(itemFromRepository.getId(), notNullValue());
        assertThat(itemFromRepository.getRequest(), notNullValue());
        assertThat(request.getId(), is(itemFromRepository.getRequest().getId()));
        assertThat(request.getRequestor().getId(), is(itemFromRepository.getRequest().getRequestor().getId()));
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

        List<ItemDto> itemDtoList = service.searchItemForRent("gReaT", 0, 10);

        assertThat(itemDto.getId(), is(itemDtoList.get(0).getId()));
        assertThat(itemDto.getName(), is(itemDtoList.get(0).getName()));
        assertThat(itemDto.getDescription(), is(itemDtoList.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), is(itemDtoList.get(0).getAvailable()));
    }

    @Test
    void searchItemForRentWithEmptyInputTest() {
        assertThat(repository.findAll(), notNullValue());

        List<ItemDto> itemDtoList = service.searchItemForRent("", 0, 10);

        assertThat(itemDtoList, empty());
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

    private ItemDto makeItemDto(Integer requestId, String name, String description, Boolean available) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder();

        builder.requestId(requestId);
        builder.name(name);
        builder.description(description);
        builder.available(available);

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
