package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.mapper.RequestMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class RequestServiceImplTest {
    private final RequestService service;
    private final RequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final RequestMapper mapper;
    private final UserMapper userMapper;
    private UserDto userDto;
    private ItemRequestDto requestDto;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        User user = userRepository.save(userToSave);
        userDto = userMapper.toDto(user);

        ItemRequestDto requestToSave = makeRequestDto("I'm looking for a hammer");
        requestDto = service.createItemRequest(userDto.getId(), requestToSave);
        request = mapper.toModel(requestDto);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void findAllItemRequestsByUserWithoutResponseItemsTest() {
        Integer expectedSize = 1;

        assertThat(repository.findAll(), notNullValue());

        List<ItemRequestInfo> allRequests = service.findAllItemRequestsByUser(userDto.getId());

        assertThat(allRequests.size(), is(expectedSize));
        assertThat(requestDto.getId(), is(allRequests.get(0).getId()));
        assertThat(allRequests.get(0).getItems(), empty());
        assertThat(requestDto.getDescription(), is(allRequests.get(0).getDescription()));
        assertThat(requestDto.getCreated(), is(allRequests.get(0).getCreated()));
    }

    @Test
    void findAllItemRequestsByUserWithResponseItemsTest() {
        Integer expectedSize = 1;

        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Thor", "mymail@email.com");
        User user = userRepository.save(userToSave);
        ItemRequest request = mapper.toModel(requestDto);
        Item item = makeItem("A hammer", "For something", true, user, request);
        itemRepository.save(item);

        List<ItemRequestInfo> allRequests = service.findAllItemRequestsByUser(userDto.getId());

        assertThat(allRequests.size(), is(expectedSize));
        assertThat(requestDto.getId(), is(allRequests.get(0).getId()));
        assertThat(allRequests.get(0).getItems().get(0).getName(), is(item.getName()));
        assertThat(allRequests.get(0).getItems().get(0).getDescription(), is(item.getDescription()));
        assertThat(allRequests.get(0).getItems().get(0).getRequestId(), is(item.getRequest().getId()));
        assertThat(requestDto.getDescription(), is(allRequests.get(0).getDescription()));
        assertThat(requestDto.getCreated(), is(allRequests.get(0).getCreated()));
    }

    @Test
    void findAllItemRequestsWithoutResponseItemsTest() {
        Integer expectedSize = 1;

        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Thor", "mymail@email.com");
        User user = userRepository.save(userToSave);
        UserDto userDto = userMapper.toDto(user);

        List<ItemRequestInfo> allRequests = service.findAllItemRequests(userDto.getId(), 0, 10);

        assertThat(allRequests.size(), is(expectedSize));
        assertThat(requestDto.getId(), is(allRequests.get(0).getId()));
        assertThat(allRequests.get(0).getItems(), empty());
        assertThat(requestDto.getDescription(), is(allRequests.get(0).getDescription()));
        assertThat(requestDto.getCreated(), is(allRequests.get(0).getCreated()));
    }

    @Test
    void findAllItemRequestsWithResponseItemsTest() {
        Integer expectedSize = 1;

        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Thor", "mymail@email.com");
        User user = userRepository.save(userToSave);
        UserDto userDto = userMapper.toDto(user);
        Item item = makeItem("A hammer", "For something", true, user, request);
        itemRepository.save(item);

        List<ItemRequestInfo> allRequests = service.findAllItemRequests(userDto.getId(), 0, 10);

        assertThat(allRequests.size(), is(expectedSize));
        assertThat(requestDto.getId(), is(allRequests.get(0).getId()));
        assertThat(allRequests.get(0).getItems().get(0).getName(), is(item.getName()));
        assertThat(allRequests.get(0).getItems().get(0).getDescription(), is(item.getDescription()));
        assertThat(allRequests.get(0).getItems().get(0).getRequestId(), is(item.getRequest().getId()));
        assertThat(requestDto.getDescription(), is(allRequests.get(0).getDescription()));
        assertThat(requestDto.getCreated(), is(allRequests.get(0).getCreated()));
    }

    @Test
    void findItemRequestWithoutResponseItemsTest() {
        assertThat(repository.findAll(), notNullValue());

        ItemRequestInfo requestInfo = service.findItemRequest(userDto.getId(), requestDto.getId());

        assertThat(requestDto.getId(), is(requestInfo.getId()));
        assertThat(requestDto.getDescription(), is(requestInfo.getDescription()));
        assertThat(requestDto.getCreated(), is(requestInfo.getCreated()));
        assertThat(requestInfo.getItems(), empty());
    }

    @Test
    void findItemRequestWithResponseItemsTest() {
        assertThat(repository.findAll(), notNullValue());

        User userToSave = makeUser("Thor", "mymail@email.com");
        User user = userRepository.save(userToSave);
        UserDto userDto = userMapper.toDto(user);
        Item item = makeItem("A hammer", "For something", true, user, request);
        itemRepository.save(item);

        ItemRequestInfo requestInfo = service.findItemRequest(userDto.getId(), requestDto.getId());

        assertThat(requestDto.getId(), is(requestInfo.getId()));
        assertThat(requestDto.getDescription(), is(requestInfo.getDescription()));
        assertThat(requestDto.getCreated(), is(requestInfo.getCreated()));
        assertThat(requestInfo.getItems().get(0).getName(), is(item.getName()));
        assertThat(requestInfo.getItems().get(0).getDescription(), is(item.getDescription()));
        assertThat(requestInfo.getItems().get(0).getAvailable(), is(item.getAvailable()));
    }

    @Test
    void verifyFindItemRequestException() {
        Integer wrongRequestId = 100;

        assertThat(repository.findAll(), notNullValue());

        final RequestNotFoundException exception = assertThrows(
                RequestNotFoundException.class,
                () -> service.findItemRequest(userDto.getId(), wrongRequestId));

        assertThat("The item request doesn't exist", is(exception.getMessage()));
    }

    private Item makeItem(String name, String description, Boolean available, User owner, ItemRequest itemRequest) {
        Item.ItemBuilder builder = Item.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);
        builder.owner(owner);
        builder.request(itemRequest);

        return builder.build();
    }

    private ItemRequestDto makeRequestDto(String description) {
        ItemRequestDto.ItemRequestDtoBuilder builder = ItemRequestDto.builder();

        builder.description(description);
        builder.created(LocalDateTime.now());

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
        entityManager.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
