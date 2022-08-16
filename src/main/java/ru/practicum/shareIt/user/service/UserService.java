package ru.practicum.shareIt.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.entity.UserDto;

import java.util.List;

@Service
public interface UserService {

    User getUser(long userId);

    User createUser(User user);

    UserDto updateUser(long userId, User user);

    List<User> getUsers();

    void removeUser(long userId);
}
