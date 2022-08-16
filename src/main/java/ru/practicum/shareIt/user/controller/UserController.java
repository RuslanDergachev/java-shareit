package ru.practicum.shareIt.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.user.service.UserService;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.entity.UserDto;
import ru.practicum.shareIt.user.entity.UserMapper;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Текущее количество пользователей: {}", userService.getUsers().size());
        return userService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.debug("Добавлен пользователь: {}", user);
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        log.debug("Обновлён пользователь: {}", userDto);
        return userService.updateUser(id, UserMapper.toUser(userDto));
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(@PathVariable long id) {
        userService.removeUser(id);
    }
}
