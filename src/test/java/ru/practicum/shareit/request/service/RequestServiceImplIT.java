package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplIT {
    private final EntityManager entityManager;
    private final RequestService service;
    private final UserService userService;
    private final RequestMapper mapper;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;

    @BeforeEach
    void setUp() {
        UserDto userToSave1 = UserDto
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        UserDto userToSave2 = UserDto
                .builder()
                .name("James")
                .email("mymail@email.com")
                .build();

        UserDto userToSave3 = UserDto
                .builder()
                .name("John")
                .email("yahoo@mail.com")
                .build();

        user1 = userService.createUser(userToSave1);
        user2 = userService.createUser(userToSave2);
        user3 = userService.createUser(userToSave3);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void createItemRequestTest() {
        ItemRequestDto requestDto = makeRequestDto("I need a hammer");
        service.createItemRequest(user1.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager
                .createQuery("select i from ItemRequest i where i.description = :description", ItemRequest.class);
        ItemRequest request = query
                .setParameter("description", requestDto.getDescription()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    void findAllItemRequestsTest() {
        Integer expectedSize = 3;

        assertThat(service.findAllItemRequests(user1.getId(), PageRequest.of(0,10)), empty());

        ItemRequestDto requestDto1 = makeRequestDto("I need a hammer");
        ItemRequestDto requestDto2 = makeRequestDto("I need a screw driver");
        ItemRequestDto requestDto3 = makeRequestDto("I need some stuff");
        service.createItemRequest(user1.getId(), requestDto1);
        service.createItemRequest(user2.getId(), requestDto2);
        service.createItemRequest(user3.getId(), requestDto3);

        TypedQuery<ItemRequest> query = entityManager
                .createQuery("select i from ItemRequest i", ItemRequest.class);
        List<ItemRequest> requests = query.getResultList();
        List<ItemRequestDto> requestDtoList = requests.stream().map(mapper::toDto).collect(Collectors.toList());

        assertThat(requestDtoList.size(), equalTo(expectedSize));
        assertThat(requestDto1.getDescription(), equalTo(requestDtoList.get(0).getDescription()));
        assertThat(requestDto2.getDescription(), equalTo(requestDtoList.get(1).getDescription()));
        assertThat(requestDto3.getDescription(), equalTo(requestDtoList.get(2).getDescription()));
        assertThat(requestDto1.getCreated(), equalTo(requestDtoList.get(0).getCreated()));
        assertThat(requestDto2.getCreated(), equalTo(requestDtoList.get(1).getCreated()));
        assertThat(requestDto3.getCreated(), equalTo(requestDtoList.get(2).getCreated()));
    }

    private ItemRequestDto makeRequestDto(String description) {
        ItemRequestDto.ItemRequestDtoBuilder builder = ItemRequestDto.builder();

        builder.description(description);

        return builder.build();
    }

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
