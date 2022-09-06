package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);

}
