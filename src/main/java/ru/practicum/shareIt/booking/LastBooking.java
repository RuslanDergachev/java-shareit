package ru.practicum.shareIt.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LastBooking {
    Long id;
    Long bookerId;
}
