package ru.practicum.shareIt.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NextBooking {
    Long id;
    Long bookerId;
}
