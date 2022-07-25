package ru.practicum.shareIt.request;

import lombok.Data;

import java.util.Date;

@Data
public class ItemRequest {
    long id;
    String description;
    long requestor;
    Date created;


}
