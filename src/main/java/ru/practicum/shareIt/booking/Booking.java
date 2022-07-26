package ru.practicum.shareIt.booking;

import lombok.Data;
import java.util.Date;

@Data
public class Booking {
    private long id;
    private Date start;
    private Date end;
    private String item;
    private long booker;
    private BookingStatus status;

}
