package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllCurrentByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 and b.end < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllPastByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 and b.start > current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllFutureByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 and b.status = 'WAITING' " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllWaitingStatusByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where b.booker = ?1 and b.status = 'REJECTED' " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllRejectedStatusByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllCurrentByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "and b.end < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllPastByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "and b.start > current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllFutureByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "and b.status = 'WAITING' " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllWaitingStatusByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingInfo" +
            "(b.id, b.itemId, i.name, b.start, b.end, b.booker, u.name, b.status) " +
            "from Booking b " +
            "inner join Item i on i.id = b.itemId " +
            "inner join User u on u.id = b.booker " +
            "where i.owner = ?1 " +
            "and b.status = 'REJECTED' " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingInfo> findAllRejectedStatusByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.LastBooking" +
            "(b1.id, b1.booker, b1.end) " +
            "from Booking b1 " +
            "where b1.itemId = ?1 " +
            "and " +
            "b1.end = (select max (b2.end) from Booking b2 where b2.end < current_timestamp)")
    LastBooking findLastBooking(Integer itemId);

    @Query("select new ru.practicum.shareit.booking.dto.NextBooking" +
            "(b1.id, b1.booker, b1.start) " +
            "from Booking b1 " +
            "where b1.itemId = ?1 " +
            "and " +
            "b1.start = (select min (b2.start) from Booking b2 where b2.start > current_timestamp)")
    NextBooking findNextBooking(Integer itemId);

    @Query("select distinct (b) " +
            "from Booking b " +
            "where b.booker = ?1 " +
            "and b.itemId = ?2 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.APPROVED " +
            "and b.end < current_timestamp ")
    List<Optional<Booking>> findByBookerAndItem(Integer userId, Integer itemId);
}