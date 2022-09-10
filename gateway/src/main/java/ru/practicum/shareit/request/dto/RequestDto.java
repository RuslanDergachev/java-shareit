package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<Item> items;
}
