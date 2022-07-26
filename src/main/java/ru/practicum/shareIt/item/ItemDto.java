package ru.practicum.shareIt.item;

import lombok.Data;
import ru.practicum.shareIt.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;


    public ItemDto(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
