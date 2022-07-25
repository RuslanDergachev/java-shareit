package ru.practicum.shareIt.item;

import java.util.List;

public interface ItemRepository {
    List<ItemDto> findByUserId(long userId);

    Item save(long userId, Item item);

    List<Item> getListItems();

    ItemDto getItemById(long itemId);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, Item item);

    List<ItemDto> searchItem(String search);
}
