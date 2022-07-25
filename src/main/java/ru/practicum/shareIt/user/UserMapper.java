package ru.practicum.shareIt.user;

public class UserMapper {

    public static UserDto toUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
