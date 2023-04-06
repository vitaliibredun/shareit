package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRepository repository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private User user1;
    private User user2;
    private User user3;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

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

        request1 = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        request2 = ItemRequest
                .builder()
                .description("I want some too")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();

        request3 = ItemRequest
                .builder()
                .description("I want some maybe")
                .requestor(user3)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyRepositoryIsEmpty() {
        List<Item> items = repository.findAll();

        assertThat(items, empty());
    }

    @Test
    void verifyPersistingOfItem() {
        Item item = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        assertThat(entityManager.getId(item), nullValue());
        entityManager.persist(item);
        assertThat(entityManager.getId(item), notNullValue());
    }

    @Test
    void verifyItemSaveInRepository() {
        Item item = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        assertThat(entityManager.getId(item), nullValue());
        repository.save(item);
        assertThat(entityManager.getId(item), notNullValue());
    }

    @Test
    void verifyItemIsSameEntity() {
        Item item = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        assertThat(entityManager.getId(item), nullValue());
        Item itemFromRepository = repository.save(item);
        assertThat(entityManager.getId(item), notNullValue());
        assertThat(item.getName(), sameInstance(itemFromRepository.getName()));
        assertThat(item.getDescription(), sameInstance(itemFromRepository.getDescription()));
        assertThat(item.getAvailable(), sameInstance(itemFromRepository.getAvailable()));
        assertThat(item.getOwner(), sameInstance(itemFromRepository.getOwner()));
        assertThat(item.getRequest(), sameInstance(itemFromRepository.getRequest()));
    }

    @Test
    void verifyRepositorySaveSameQuantityOfEntities() {
        Integer expectedSize = 2;

        Item item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        Item item2 = Item
                .builder()
                .name("Item2")
                .description("For something else")
                .available(false)
                .owner(user2)
                .request(request2)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(item1), nullValue());
        assertThat(entityManager.getId(item2), nullValue());
        repository.save(item1);
        repository.save(item2);
        assertThat(entityManager.getId(item1), notNullValue());
        assertThat(entityManager.getId(item2), notNullValue());
        assertThat(repository.findAll().size(), equalTo(expectedSize));
    }

    @Test
    void verifySearchItemByInput() {
        Integer expectedSize = 1;

        Item item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        Item item2 = Item
                .builder()
                .name("Item2")
                .description("For something else")
                .available(true)
                .owner(user2)
                .request(request2)
                .build();

        Item item3 = Item
                .builder()
                .name("Item3")
                .description("For something cool")
                .available(true)
                .owner(user3)
                .request(request3)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(item1), nullValue());
        assertThat(entityManager.getId(item2), nullValue());
        assertThat(entityManager.getId(item3), nullValue());
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        assertThat(entityManager.getId(item1), notNullValue());
        assertThat(entityManager.getId(item2), notNullValue());
        assertThat(entityManager.getId(item3), notNullValue());
        List<Item> itemsFromRepository = repository.searchItemByInput("else", PageRequest.of(0, 10));
        assertThat(itemsFromRepository.size(), equalTo(expectedSize));
        assertThat(item2.getDescription(), sameInstance(itemsFromRepository.get(0).getDescription()));
        assertThat(item2.getName(), sameInstance(itemsFromRepository.get(0).getName()));
        assertThat(item2.getAvailable(), sameInstance(itemsFromRepository.get(0).getAvailable()));
        assertThat(item2.getOwner(), sameInstance(itemsFromRepository.get(0).getOwner()));
    }

    @Test
    void verifyFindAllByOwner() {
        Integer expectedSize = 2;
        Integer userId = user1.getId();

        Item item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user3)
                .request(request1)
                .build();

        Item item2 = Item
                .builder()
                .name("Item2")
                .description("For something else")
                .available(true)
                .owner(user1)
                .request(request2)
                .build();

        Item item3 = Item
                .builder()
                .name("Item3")
                .description("For something cool")
                .available(true)
                .owner(user1)
                .request(request3)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(item1), nullValue());
        assertThat(entityManager.getId(item2), nullValue());
        assertThat(entityManager.getId(item3), nullValue());
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        assertThat(entityManager.getId(item1), notNullValue());
        assertThat(entityManager.getId(item2), notNullValue());
        assertThat(entityManager.getId(item3), notNullValue());
        List<Item> itemsFromRepository = repository.findAllByOwner(userId, PageRequest.of(0, 10));
        assertThat(itemsFromRepository.size(), equalTo(expectedSize));
        assertThat(item2.getDescription(), sameInstance(itemsFromRepository.get(0).getDescription()));
        assertThat(item3.getDescription(), sameInstance(itemsFromRepository.get(1).getDescription()));
        assertThat(item2.getOwner(), sameInstance(itemsFromRepository.get(0).getOwner()));
        assertThat(item3.getOwner(), sameInstance(itemsFromRepository.get(1).getOwner()));
        assertThat(item2.getName(), sameInstance(itemsFromRepository.get(0).getName()));
        assertThat(item3.getName(), sameInstance(itemsFromRepository.get(1).getName()));
    }

    @Test
    void verifyFindItemsByRequest() {
        Integer expectedSize = 1;
        Integer requestId = request3.getId();

        Item item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();

        Item item2 = Item
                .builder()
                .name("Item2")
                .description("For something else")
                .available(true)
                .owner(user2)
                .request(request2)
                .build();

        Item item3 = Item
                .builder()
                .name("Item3")
                .description("For something cool")
                .available(true)
                .owner(user3)
                .request(request3)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(item1), nullValue());
        assertThat(entityManager.getId(item2), nullValue());
        assertThat(entityManager.getId(item3), nullValue());
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        assertThat(entityManager.getId(item1), notNullValue());
        assertThat(entityManager.getId(item2), notNullValue());
        assertThat(entityManager.getId(item3), notNullValue());
        List<ItemDto> itemsFromRepository = repository.findItemsByRequest(requestId);
        assertThat(itemsFromRepository.size(), equalTo(expectedSize));
        assertThat(item3.getRequest(), sameInstance(request3));
        assertThat(request3.getId(), sameInstance(itemsFromRepository.get(0).getRequestId()));
        assertThat(item3.getName(), sameInstance(itemsFromRepository.get(0).getName()));
    }

    private void resetIdColumns() {
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
