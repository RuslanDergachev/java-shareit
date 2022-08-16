package ru.practicum.shareIt.item.entity;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .available(item.getAvailable())
                .start(null)
                .end(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item toItem(long ownerId, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId,
                null
        );
    }
}
