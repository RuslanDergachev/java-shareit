package ru.practicum.shareit.item.entity;

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
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toItem(long ownerId, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .requestId(itemDto.getRequestId())
                .build();
    }
}
