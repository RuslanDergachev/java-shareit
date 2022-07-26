package ru.practicum.shareIt.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareIt.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
