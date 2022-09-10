package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Validation;

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
        log.info("Текущее количество пользователей: ");
        return userClient.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        Validation.validateUserDto(userDto);
        log.info("Добавлен пользователь: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserDto userDto) {
        Validation.validateId(id);
        log.info("Обновлён пользователь: {}", userDto);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        Validation.validateId(id);
        return userClient.getUser(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(@PathVariable long id) {
        Validation.validateId(id);
        userClient.removeUser(id);
    }
}
