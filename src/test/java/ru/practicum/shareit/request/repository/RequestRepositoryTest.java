package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
public class RequestRepositoryTest {
    private final TestEntityManager entityManager;
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private User user1;
    private User user2;

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

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyRepositoryIsEmpty() {
        List<ItemRequest> itemRequests = repository.findAll();

        assertThat(itemRequests, empty());
    }

    @Test
    void verifyPersistingOfItemRequest() {
        ItemRequest request = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        assertThat(entityManager.getId(request), nullValue());
        entityManager.persist(request);
        assertThat(entityManager.getId(request), notNullValue());
    }

    @Test
    void verifyRequestSaveInRepository() {
        ItemRequest request = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        assertThat(entityManager.getId(request), nullValue());
        repository.save(request);
        assertThat(entityManager.getId(request), notNullValue());
    }

    @Test
    void verifyItemRequestIsSameEntity() {
        ItemRequest request = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        assertThat(entityManager.getId(request), nullValue());
        ItemRequest requestFromRepository = repository.save(request);
        assertThat(entityManager.getId(request), notNullValue());
        assertThat(request.getDescription(), sameInstance(requestFromRepository.getDescription()));
        assertThat(request.getCreated(), sameInstance(requestFromRepository.getCreated()));
        assertThat(request.getRequestor(), sameInstance(requestFromRepository.getRequestor()));
    }

    @Test
    void verifyRepositorySaveSameQuantityOfEntities() {
        Integer expectedSize = 2;

        ItemRequest request1 = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest
                .builder()
                .description("I want some too")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(request1), nullValue());
        assertThat(entityManager.getId(request2), nullValue());
        repository.save(request1);
        repository.save(request2);
        assertThat(entityManager.getId(request1), notNullValue());
        assertThat(entityManager.getId(request2), notNullValue());
        assertThat(repository.findAll().size(), equalTo(expectedSize));
    }

    @Test
    void verifyFindRequestsByRequestor() {
        Integer expectedSize = 2;
        Integer expectedUserId = 1;

        ItemRequest request1 = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest
                .builder()
                .description("I want some too")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        assertThat(repository.findAllBuRequestor(expectedUserId), empty());
        assertThat(entityManager.getId(request1), nullValue());
        assertThat(entityManager.getId(request2), nullValue());
        repository.save(request1);
        repository.save(request2);
        assertThat(entityManager.getId(request1), notNullValue());
        assertThat(entityManager.getId(request2), notNullValue());
        List<ItemRequest> requestsFromRepository = repository.findAllBuRequestor(expectedUserId);
        assertThat(requestsFromRepository.size(), equalTo(expectedSize));
        assertThat(request1.getRequestor(), sameInstance(requestsFromRepository.get(0).getRequestor()));
        assertThat(request2.getRequestor(), sameInstance(requestsFromRepository.get(1).getRequestor()));
        assertThat(request1.getDescription(), sameInstance(requestsFromRepository.get(1).getDescription()));
        assertThat(request2.getDescription(), sameInstance(requestsFromRepository.get(0).getDescription()));
    }

    @Test
    void verifyFindRequestsExceptRequestor() {
        Integer expectedSize = 1;
        Integer expectedUserId = 1;

        ItemRequest request1 = ItemRequest
                .builder()
                .description("I want some thing")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest
                .builder()
                .description("I want some too")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request3 = ItemRequest
                .builder()
                .description("I'm looking forward'")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();

        assertThat(repository.findAllBuRequestor(expectedUserId), empty());
        assertThat(entityManager.getId(request1), nullValue());
        assertThat(entityManager.getId(request2), nullValue());
        repository.save(request1);
        repository.save(request2);
        repository.save(request3);
        assertThat(entityManager.getId(request1), notNullValue());
        assertThat(entityManager.getId(request2), notNullValue());
        assertThat(entityManager.getId(request3), notNullValue());
        List<ItemRequest> requestsFromRepository = repository.findAllBuRequestor(expectedUserId);
        assertThat(requestsFromRepository.size(), equalTo(expectedSize));
        assertThat(request1.getRequestor(), sameInstance(requestsFromRepository.get(0).getRequestor()));
        assertThat(request1.getDescription(), sameInstance(requestsFromRepository.get(0).getDescription()));
    }

    private void resetIdColumns() {
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
