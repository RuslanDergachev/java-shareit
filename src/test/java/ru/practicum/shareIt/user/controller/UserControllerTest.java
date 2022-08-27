package ru.practicum.shareIt.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.entity.UserDto;
import ru.practicum.shareIt.user.entity.UserMapper;
import ru.practicum.shareIt.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private User user = new User(1L, "Vasya", "vasya@mail.ru");
    private UserDto userDto = UserMapper.toUserDto(user);
    private List<User> allUsers = List.of(user);

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getUsers())
                .thenReturn(allUsers);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Vasya")));
    }

    @Test
    void createNewUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        mvc.perform(get("/users/1")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Vasya")));
    }

    @Test
    void removeUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }
}