package ru.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;

import java.io.IOException;
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

    public User createUser(User user) throws IOException {
        if (user.getName()== null || user.getEmail()== null) {
            log.error("Имя пользователя или email отсутствуют");
            throw new ValidationException("Нет имени или адреса почты");
        }
        userRepository.createUser(user);
        return user;
    }

    public User updateUser(long userId, User user) {
        if (userId <= 0) {
            log.error("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя равен 0");
        }
        return userRepository.updateUser(userId, user);
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public void removeUser(long userId) {
        if (userId <= 0) {
            log.error("ID пользователя равен 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        userRepository.removeUser(userId);
    }
}
