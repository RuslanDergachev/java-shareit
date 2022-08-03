package ru.practicum.shareIt.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserMapper {

    public static UserDto toUserDto(User user){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd, hh:mm:ss");
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public static User toUser(UserDto userDto){
        User user = new User();
        user.setId(user.getId());
        user.setName(user.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }
}
