package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingFromRepository;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllCurrentByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllPastByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllFutureByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.WAITING " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllWaitingStatusByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.REJECTED " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllRejectedStatusByBooker(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllCurrentByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllPastByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > current_timestamp " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllFutureByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.WAITING " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllWaitingStatusByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.REJECTED " +
            "group by b.id " +
            "order by b.start desc")
    List<BookingFromRepository> findAllRejectedStatusByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.booking.dto.LastBooking" +
            "(b1.id, b1.booker.id, b1.start) " +
            "from Booking b1 " +
            "where b1.item.id = ?1 " +
            "and " +
            "b1.start = (select max (b2.start) from Booking b2 where b2.start < current_timestamp)")
    LastBooking findLastBooking(Integer itemId);

    @Query("select new ru.practicum.shareit.booking.dto.NextBooking" +
            "(b1.id, b1.booker.id, b1.start) " +
            "from Booking b1 " +
            "where b1.item.id = ?1 " +
            "and " +
            "b1.start = (select min (b2.start) from Booking b2 where b2.start > current_timestamp)")
    NextBooking findNextBooking(Integer itemId);

    @Query("select distinct (b) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.item.id = ?2 " +
            "and b.status = ru.practicum.shareit.booking.model.constants.BookingStatus.APPROVED " +
            "and b.end < current_timestamp ")
    List<Optional<Booking>> findByBookerAndItem(Integer userId, Integer itemId);
}