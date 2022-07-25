package ru.practicum.shareIt.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {

    long id;
    String name;
    @Email
    String email;
}
