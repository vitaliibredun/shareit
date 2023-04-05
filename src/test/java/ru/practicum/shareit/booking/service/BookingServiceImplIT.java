package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constants.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

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
public class BookingServiceImplIT {
    private final EntityManager entityManager;
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper mapper;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    private ItemDto item1;
    private ItemDto item2;
    private ItemDto item3;

    @BeforeEach
    void setUp() {
        UserDto userToSave1 = makeUserDto("Bond", "email@mail.com");
        UserDto userToSave2 = makeUserDto("James", "mymail@email.com");
        UserDto userToSave3 = makeUserDto("John", "yahoo@mail.com");
        user1 = userService.createUser(userToSave1);
        user2 = userService.createUser(userToSave2);
        user3 = userService.createUser(userToSave3);

        ItemDto itemToSave1 = makeItemDto("Item1", "For something", true);
        ItemDto itemToSave2 = makeItemDto("Item2", "For something else", true);
        ItemDto itemToSave3 = makeItemDto("Item3", "For somebody", true);
        item1 = itemService.createItem(user1.getId(), itemToSave1);
        item2 = itemService.createItem(user2.getId(), itemToSave2);
        item3 = itemService.createItem(user3.getId(), itemToSave3);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDto = makeBookingDto(item3.getId(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        service.createBooking(user1.getId(), bookingDto);

        TypedQuery<Booking> query = entityManager
                .createQuery("select b from Booking b where b.booker.id = :bookerId", Booking.class);
        Booking booking = query
                .setParameter("bookerId", user1.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void findAllBookingsCustomerTest() {
        Integer userId = user1.getId();
        Integer expectedSize = 2;

        assertThat(service.findAllBookingsCustomer(user1.getId(), "FUTURE", PageRequest.of(0, 10)), empty());

        BookingDto bookingDto1 = makeBookingDto(item2.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3));
        BookingDto bookingDto2 = makeBookingDto(item3.getId(),
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10));
        BookingDto bookingDto3 = makeBookingDto(item1.getId(),
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10));
        service.createBooking(user1.getId(), bookingDto1);
        service.createBooking(user1.getId(), bookingDto2);
        service.createBooking(user3.getId(), bookingDto3);

        TypedQuery<Booking> query = entityManager
                .createQuery("select b from Booking b where b.booker.id = :userId", Booking.class);
        List<Booking> bookings = query
                .setParameter("userId", userId).getResultList();
        List<BookingInfo> bookingInfoList = bookings.stream().map(mapper::toDto).collect(Collectors.toList());

        assertThat(bookingInfoList.size(), equalTo(expectedSize));
        assertThat(bookingDto1.getItemId(), equalTo(bookingInfoList.get(0).getItem().getId()));
        assertThat(bookingDto2.getItemId(), equalTo(bookingInfoList.get(1).getItem().getId()));
        assertThat(bookingDto1.getStart(), equalTo(bookingInfoList.get(0).getStart()));
        assertThat(bookingDto2.getStart(), equalTo(bookingInfoList.get(1).getStart()));
    }

    @Test
    void findAllBookingsOwnerTest() {
        Integer userId = user1.getId();
        Integer expectedSize = 1;

        assertThat(service.findAllBookingsOwner(user1.getId(), "FUTURE", PageRequest.of(0, 10)), empty());

        BookingDto bookingDto1 = makeBookingDto(item2.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3));
        BookingDto bookingDto2 = makeBookingDto(item3.getId(),
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10));
        BookingDto bookingDto3 = makeBookingDto(item1.getId(),
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(10));
        service.createBooking(user1.getId(), bookingDto1);
        service.createBooking(user1.getId(), bookingDto2);
        service.createBooking(user3.getId(), bookingDto3);

        TypedQuery<Booking> query = entityManager
                .createQuery("select b from Booking b where b.item.owner.id = :userId", Booking.class);
        List<Booking> bookings = query
                .setParameter("userId", userId).getResultList();
        List<BookingInfo> bookingInfoList = bookings.stream().map(mapper::toDto).collect(Collectors.toList());

        assertThat(bookingInfoList.size(), equalTo(expectedSize));
        assertThat(bookingDto3.getItemId(), equalTo(bookingInfoList.get(0).getItem().getId()));
        assertThat(bookingDto3.getStart(), equalTo(bookingInfoList.get(0).getStart()));
        assertThat(bookingInfoList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);

        return builder.build();
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto.UserDtoBuilder builder = UserDto.builder();

        builder.name(name);
        builder.email(email);

        return builder.build();
    }

    private BookingDto makeBookingDto(Integer itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto.BookingDtoBuilder builder = BookingDto.builder();

        builder.itemId(itemId);
        builder.start(start);
        builder.end(end);

        return builder.build();
    }

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
