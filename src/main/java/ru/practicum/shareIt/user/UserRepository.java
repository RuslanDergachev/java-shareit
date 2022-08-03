package ru.practicum.shareIt.user;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


public interface UserRepository {

    Optional<User> findUserById(long userId);

    User createUser(User user);

    User updateUser(long userId, User user);

    void removeUser(long userId);

    List<User> getUsers();

    List<User> findAll();
}
