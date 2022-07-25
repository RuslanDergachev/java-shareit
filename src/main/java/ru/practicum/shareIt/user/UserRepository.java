package ru.practicum.shareIt.user;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserById(long userId);

    User createUser(User user) throws IOException;

    User updateUser(long userId, User user);

    void removeUser(long userId);

    List<User> getUsers();
}
