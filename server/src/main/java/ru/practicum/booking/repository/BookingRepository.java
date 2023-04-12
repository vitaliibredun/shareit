package ru.practicum.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.dto.BookingFromRepository;
import ru.practicum.booking.dto.LastBooking;
import ru.practicum.booking.dto.NextBooking;
import ru.practicum.booking.model.Booking;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1  " +
            "group by b.id")
    List<BookingFromRepository> findAllByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllCurrentByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllPastByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllFutureByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ru.practicum.booking.constants.BookingStatus.WAITING " +
            "group by b.id")
    List<BookingFromRepository> findAllWaitingStatusByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ru.practicum.booking.constants.BookingStatus.REJECTED " +
            "group by b.id")
    List<BookingFromRepository> findAllRejectedStatusByBooker(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "group by b.id")
    List<BookingFromRepository> findAllByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end > current_timestamp " +
            "and b.start < current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllCurrentByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllPastByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > current_timestamp " +
            "group by b.id")
    List<BookingFromRepository> findAllFutureByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ru.practicum.booking.constants.BookingStatus.WAITING " +
            "group by b.id")
    List<BookingFromRepository> findAllWaitingStatusByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.BookingFromRepository" +
            "(b.id, b.item.id, b.item.name, b.start, b.end, b.booker.id, b.booker.name, b.status) " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ru.practicum.booking.constants.BookingStatus.REJECTED " +
            "group by b.id")
    List<BookingFromRepository> findAllRejectedStatusByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.booking.dto.LastBooking" +
            "(b1.id, b1.booker.id, b1.start) " +
            "from Booking b1 " +
            "where b1.item.id = ?1 " +
            "and " +
            "b1.start = (select max (b2.start) from Booking b2 where b2.start < current_timestamp)")
    LastBooking findLastBooking(Integer itemId);

    @Query("select new ru.practicum.booking.dto.NextBooking" +
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
            "and b.status = ru.practicum.booking.constants.BookingStatus.APPROVED " +
            "and b.end < current_timestamp ")
    List<Optional<Booking>> findByBookerAndItem(Integer userId, Integer itemId);
}