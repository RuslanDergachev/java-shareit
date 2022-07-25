package ru.practicum.shareIt.booking;

import lombok.Data;
import java.util.Date;

@Data
public class Booking {
    long id;
    Date start;
    Date end;
    String item;
    long booker;
    BookingStatus status;

}
