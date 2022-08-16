package ru.practicum.shareIt.booking.entity;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingUpdateDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private Item item;
}
