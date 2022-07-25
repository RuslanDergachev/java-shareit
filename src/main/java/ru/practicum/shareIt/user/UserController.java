package ru.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
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
    public User create(@Valid @RequestBody User user) throws IOException {
        log.debug("Добавлен пользователь: {}", user);
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{id}")
    public User update(@PathVariable long id, @RequestBody User user) {
        log.debug("Обновлён пользователь: {}", user);
        return userService.updateUser(id, user);
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
