package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationMetods;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Текущее количество пользователей: ");
        return userClient.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        ValidationMetods.validationUserDto(userDto);
        log.debug("Добавлен пользователь: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserDto userDto) {
        ValidationMetods.validationId(id);
        log.debug("Обновлён пользователь: {}", userDto);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        ValidationMetods.validationId(id);
        return userClient.getUser(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(@PathVariable long id) {
        ValidationMetods.validationId(id);
        userClient.removeUser(id);
    }
}
