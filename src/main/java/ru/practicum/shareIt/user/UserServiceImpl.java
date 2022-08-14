package ru.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("такого пользователя нет в списке"));
        return user;
    }

    @Transactional
    public User createUser(User user) {
        if (user.getName()== null) {
            log.info("Имя пользователя отсутствует");
            throw new ValidationException("Нет имени пользователя");
        }
        if (user.getEmail()== null) {
            log.info("У пользователя отсутствует email");
            throw new ValidationException("Нет адреса почты пользователя");
        }
        userRepository.saveAndFlush(user);
        return user;
    }

    public UserDto updateUser(long userId, User user) {
        User saveUser = new User();
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя равен 0");
        }
        Optional<User> newUser = userRepository.findById(userId);
        if(newUser.isPresent()) {
            saveUser = newUser.get();
        }
        if (user.getName() != null){
            saveUser.setName(user.getName());
        }
        if (user.getEmail() != null){
            saveUser.setEmail(user.getEmail());
        }
        userRepository.saveAndFlush(saveUser);

        return UserMapper.toUserDto(saveUser);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void removeUser(long userId) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID пользователя меньше или равно 0");
        }
        userRepository.deleteById(userId);
    }
}
