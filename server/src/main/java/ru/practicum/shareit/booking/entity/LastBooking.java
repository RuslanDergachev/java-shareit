package ru.practicum.shareit.booking.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LastBooking {
    private Long id;
    private Long bookerId;
}
