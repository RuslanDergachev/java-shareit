package ru.practicum.shareIt.item;

import lombok.Data;
import ru.practicum.shareIt.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
public class ItemDto {
    long id;
    @NotBlank
    String name;
    String description;
    boolean available;


    public ItemDto(long id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
