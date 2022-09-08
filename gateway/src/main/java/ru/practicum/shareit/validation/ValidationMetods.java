package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.FalseIdException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

@Slf4j
public class ValidationMetods {

    public static void validationId(long id) {
        if (id <= 0) {
            log.debug("ID меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
    }

     public static void validationUserDto(UserDto userDto) {
         if (userDto.getName() == null) {
             log.info("Имя пользователя отсутствует");
             throw new ValidationException("Нет имени пользователя");
         }
         if (userDto.getEmail() == null) {
             log.info("У пользователя отсутствует email");
             throw new ValidationException("Нет адреса почты пользователя");
         }
    }

    public static void validationRequest(long userId, RequestDto requestDto) {
        if (requestDto.getDescription() == null) {
            log.info("Запрос пользователя {} пустой", userId);
            throw new ValidationException("Запрос пользователя ID " + userId + " пустой");
        }
    }

    public static void validationItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if (itemDto.getDescription() == null) {
            log.info("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Нет статуса доступности вещи");
            throw new ValidationException("Отсутствует статус доступности вещи");
        }
    }

}
