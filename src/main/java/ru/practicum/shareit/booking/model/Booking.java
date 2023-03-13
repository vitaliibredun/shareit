package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.model.constants.BookingStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "item_id")
    private Integer itemId;
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    @Column(name = "booker_id")
    private Integer booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
}
