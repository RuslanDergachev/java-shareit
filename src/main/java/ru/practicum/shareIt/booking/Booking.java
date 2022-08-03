package ru.practicum.shareIt.booking;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date start;
    private Date end;
    private String item;
    private long booker;
    private BookingStatus status;

}
