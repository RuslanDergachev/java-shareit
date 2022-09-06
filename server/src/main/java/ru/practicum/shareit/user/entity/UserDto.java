package ru.practicum.shareit.user.entity;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @Email
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
