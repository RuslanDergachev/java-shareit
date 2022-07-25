package ru.practicum.shareIt.item;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
    Item addNewItem(long userId, Item item);

    List<ItemDto> getItems(long userId);

    ItemDto updateItem(long userId, long itemId, Item item);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItem(long userId, String searchItem);

    void deleteItem(long userId, long itemId);
}
