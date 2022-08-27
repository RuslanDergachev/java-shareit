package ru.practicum.shareIt.request.model.dto;

import lombok.*;
import ru.practicum.shareIt.item.entity.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<Item> items;
}
