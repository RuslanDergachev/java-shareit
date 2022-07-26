package ru.practicum.shareIt.item;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addNewItem(long userId, ItemDto itemDto);

    List<ItemDto> getItems(long userId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItem(long userId, String searchItem);

    void deleteItem(long userId, long itemId);
}
