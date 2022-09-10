package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.entity.UserDto;
import ru.practicum.shareit.user.entity.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

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
        if (user.getName() == null) {
            log.warn("Имя пользователя отсутствует");
            throw new ValidationException("Нет имени пользователя");
        }
        if (user.getEmail() == null) {
            log.warn("У пользователя отсутствует email");
            throw new ValidationException("Нет адреса почты пользователя");
        }
        if(user.getId() == 0){
            user.setId(null);
        }
        userRepository.save(user);
        return user;
    }

    public UserDto updateUser(long userId, User user) {
        User saveUser = new User();
        validationUserId(userId);
        Optional<User> newUser = userRepository.findById(userId);
        if (newUser.isPresent()) {
            saveUser = newUser.get();
        }
        if (user.getName() != null) {
            saveUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
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
        validationUserId(userId);
        userRepository.deleteById(userId);
    }

    private void validationUserId(long userId) {
        if (userId <= 0) {
            log.warn("ID пользователя меньше или равен 0");
            throw new NullPointerException("ID пользователя меньше или равен 0");
        }
    }
}
