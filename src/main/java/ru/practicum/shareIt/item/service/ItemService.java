package ru.practicum.shareIt.item.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.entity.ItemDto;
import ru.practicum.shareIt.request.model.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addNewItem(long userId, ItemDto itemDto);

    List<ItemDto> getItems(long userId);

    Item updateItem(Long userId, Item item);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItem(long userId, String searchItem, int from, int size);

    void deleteItem(long userId, long itemId);

    List<Item> getItemsByRequestId(long requestId);
}
