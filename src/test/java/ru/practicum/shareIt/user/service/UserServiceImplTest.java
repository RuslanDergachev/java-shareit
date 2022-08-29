package ru.practicum.shareIt.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.entity.UserDto;
import ru.practicum.shareIt.user.entity.UserMapper;
import ru.practicum.shareIt.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnUserByIdTest() {
        when(mockUserRepository.findById(Mockito.eq(1L)))
                .thenReturn(Optional.of(new User(1L, "Ivan", "user@email.ru")));
        User newUser = new User(1L, "Ivan", "user@mail.ru");
        User user = userService.getUser(1L);

        Assertions.assertEquals(newUser.getName(), user.getName());
    }

    @Test
    void whenUserIdNotRight_thenReturnException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(2L));

        Assertions.assertEquals("такого пользователя нет в списке", exception.getMessage());
    }

    @Test
    void shouldReturnNewUserTest() {
        when(mockUserRepository.save(Mockito.any()))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        User newUser = new User(1L, "Ivan", "user@mail.ru");
        User user = userService.createUser(newUser);

        Assertions.assertEquals(newUser.getName(), user.getName());
    }

    @Test
    void whenUserNotHaveName_thenReturnException() {
        User newUser = new User(1L, null, "user@mail.ru");
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(newUser));

        Assertions.assertEquals("Нет имени пользователя", exception.getMessage());
    }

    @Test
    void whenUserNotHaveEmail_thenReturnException() {
        User newUser = new User(1L, "Vasya", null);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(newUser));

        Assertions.assertEquals("Нет адреса почты пользователя", exception.getMessage());
    }

    @Test
    void shouldReturnUpdateUserTest() {
        User user = new User(1L, "Petya", "user@email.ru");
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(mockUserRepository.saveAndFlush(user))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        UserDto newUserDto = UserMapper.toUserDto(new User(1L, "Petya", "user@email.ru"));

        Assertions.assertEquals(newUserDto.getId(), userService.updateUser(1L, user).getId());
        Assertions.assertEquals(newUserDto.getName(), userService.updateUser(1L, user).getName());
        Assertions.assertEquals(newUserDto.getEmail(), userService.updateUser(1L, user).getEmail());
    }

    @Test
    void shouldReturnUsersTest() {
        List<User> userList = List.of(new User(1L, "Ivan", "user@email.ru"),
                new User(2L, "Petr", "petr@email.ru"));
        when(mockUserRepository.findAll())
                .thenReturn(userList);

        Assertions.assertEquals(2, userService.getUsers().size());
        Assertions.assertEquals("Ivan", userService.getUsers().get(0).getName());
    }

    @Test
    void shouldRemoveUserTest() {
        userService.removeUser(1L);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void whenUserIdLessThenZero_thenReturnException() {
        final NullPointerException exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> userService.removeUser(-1L));

        Assertions.assertEquals("ID пользователя равен 0", exception.getMessage());
    }
}