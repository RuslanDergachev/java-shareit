package ru.practicum.shareIt.item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item){
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
                );
    }
}
