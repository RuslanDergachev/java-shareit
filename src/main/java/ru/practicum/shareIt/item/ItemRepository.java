package ru.practicum.shareIt.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<ItemDto> findByUserId(long userId);

    ItemDto save(long userId, ItemDto itemDto);

    List<Item> getListItems();

    ItemDto getItemById(long itemId);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String search);
}
