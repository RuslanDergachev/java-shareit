package ru.practicum.shareIt.user;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface UserService {

    User getUser(long userId);

    User createUser(User user);

    User updateUser(long userId, User user);

    List<User> getUsers();

    void removeUser(long userId);
}
