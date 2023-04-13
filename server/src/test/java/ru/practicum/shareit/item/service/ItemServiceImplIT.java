package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingInfo;
import ru.practicum.booking.service.BookingService;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.item.service.ItemService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIT {
    private final EntityManager entityManager;
    private final ItemService service;
    private final ItemMapper mapper;
    private final UserService userService;
    private final BookingService bookingService;
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

    @Test
    void createItemTest() {
        ItemDto itemDto = makeItemDto("Item1", "For something", true);
        service.createItem(user1.getId(), itemDto);

        TypedQuery<Item> query = entityManager
                .createQuery("select i from Item i where i.description = :description", Item.class);
        Item item = query
                .setParameter("description", itemDto.getDescription()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void findAllItemsByUserTest() {
        Integer userId = user1.getId();
        Integer expectedSize = 2;

        assertThat(service.findAllItemsByUser(user1.getId(), PageRequest.of(0,10)), empty());

        ItemDto itemDto1 = makeItemDto("Item1", "For something", true);
        ItemDto itemDto2 = makeItemDto("Item2", "For somebody", true);
        ItemDto itemDto3 = makeItemDto("Item3", "Nice stuff", true);
        service.createItem(user1.getId(), itemDto1);
        service.createItem(user2.getId(), itemDto2);
        service.createItem(user1.getId(), itemDto3);

        TypedQuery<Item> query = entityManager
                .createQuery("select i from Item i where i.owner.id = :userId", Item.class);
        List<Item> items = query
                .setParameter("userId", userId).getResultList();
        List<ItemDto> itemDtoList = items.stream().map(mapper::toDto).collect(Collectors.toList());

        assertThat(itemDtoList.size(), equalTo(expectedSize));
        assertThat(itemDto1.getDescription(), equalTo(itemDtoList.get(0).getDescription()));
        assertThat(itemDto3.getDescription(), equalTo(itemDtoList.get(1).getDescription()));
        assertThat(itemDto1.getName(), equalTo(itemDtoList.get(0).getName()));
        assertThat(itemDto3.getName(), equalTo(itemDtoList.get(1).getName()));
        assertThat(itemDto1.getAvailable(), equalTo(itemDtoList.get(0).getAvailable()));
        assertThat(itemDto3.getAvailable(), equalTo(itemDtoList.get(1).getAvailable()));
    }

    @Test
    void searchItemForRentTest() {
        Integer expectedSize = 1;
        Integer sizeOfAllItems = 3;

        assertThat(service.searchItemForRent("some", PageRequest.of(0,10)), empty());

        ItemDto itemDto1 = makeItemDto("Item1", "For something", false);
        ItemDto itemDto2 = makeItemDto("Item2", "For somebody", true);
        ItemDto itemDto3 = makeItemDto("Item3", "Nice stuff", true);
        service.createItem(user1.getId(), itemDto1);
        service.createItem(user2.getId(), itemDto2);
        service.createItem(user3.getId(), itemDto3);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i", Item.class);
        List<Item> items = query.getResultList();
        List<ItemDto> itemDtoList = items.stream().map(mapper::toDto).collect(Collectors.toList());
        List<ItemDto> itemsForRent = service.searchItemForRent("some", PageRequest.of(0,10));

        assertThat(itemDtoList.size(), equalTo(sizeOfAllItems));
        assertThat(itemsForRent.size(), equalTo(expectedSize));
        assertThat(itemDto2.getAvailable(), equalTo(itemsForRent.get(0).getAvailable()));
        assertThat(itemDto2.getName(), equalTo(itemsForRent.get(0).getName()));
        assertThat(itemDto2.getDescription(), equalTo(itemsForRent.get(0).getDescription()));
    }

    @Test
    void addCommentTest() {
        ItemDto itemDto = makeItemDto("Item1", "For something", true);
        ItemDto itemFromRepository = service.createItem(user1.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(itemFromRepository.getId(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        BookingInfo booking = bookingService.createBooking(user2.getId(), bookingDto);
        bookingService.approvingBooking(user1.getId(), booking.getId(), true);
        CommentDto commentDto = makeCommentDto("Thanks a lot", user2.getName());
        service.addComment(user2.getId(), itemFromRepository.getId(), commentDto);

        TypedQuery<Comment> query = entityManager
                .createQuery("select c from Comment c", Comment.class);
        Comment comment = query.getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(comment.getText()));
        assertThat(commentDto.getAuthorName(), equalTo(comment.getAuthor().getName()));
    }

    private BookingDto makeBookingDto(Integer itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.itemId(itemId);
        builder.start(start);
        builder.end(end);

        return builder.build();
    }

    private CommentDto makeCommentDto(String text, String authorName) {
        CommentDto.CommentDtoBuilder builder = CommentDto.builder();

        builder.text(text);
        builder.authorName(authorName);

        return builder.build();
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);

        return builder.build();
    }
}
