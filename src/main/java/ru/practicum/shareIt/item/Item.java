package ru.practicum.shareIt.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareIt.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    long id;

    String name;

    String description;
    Boolean available;
    long owner;
    ItemRequest request;
}
