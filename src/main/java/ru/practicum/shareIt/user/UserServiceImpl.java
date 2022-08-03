package ru.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
        return user;
    }

    public User createUser(User user) {
        if (user.getName() == null) {
            log.info("Имя пользователя отсутствует");
            throw new ValidationException("Нет имени пользователя");
        }
        if (user.getEmail()== null) {
            log.info("У пользователя отсутствует email");
            throw new ValidationException("Нет адреса почты пользователя");
        }
        userRepository.createUser(user);
        return user;
    }

    public User updateUser(long userId, User user) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя равен 0");
        }
        return userRepository.updateUser(userId, user);
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public void removeUser(long userId) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя меньше или равно 0");
        }
        userRepository.removeUser(userId);
    }
}
