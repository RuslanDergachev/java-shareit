package ru.practicum.shareIt.request.model.dto;

import ru.practicum.shareIt.request.model.entity.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestorId())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestorId(itemRequestDto.getRequestor())
                .build();
    }
}
