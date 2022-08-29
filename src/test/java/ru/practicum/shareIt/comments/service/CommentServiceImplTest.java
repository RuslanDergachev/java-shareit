package ru.practicum.shareIt.comments.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareIt.booking.entity.Booking;
import ru.practicum.shareIt.booking.repository.BookingRepository;
import ru.practicum.shareIt.comments.entity.Comment;
import ru.practicum.shareIt.comments.entity.CommentDto;
import ru.practicum.shareIt.comments.repository.CommentRepository;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.entity.ItemMapper;
import ru.practicum.shareIt.item.service.ItemService;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private static final Comment COMMENT = Comment.builder()
            .id(1L)
            .text("Какой-то текст")
            .authorId(1L)
            .build();
    @Test
    void shouldReturnNewCommentTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Перфоратор электрический")
                .description("Mocito")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .build();
        Mockito
                .when(bookingRepository.getBookingsByItemById(1L))
                .thenReturn(List.of(booking));
        Mockito
                .when(itemService.getItemById(1L, 1L))
                .thenReturn(ItemMapper.toItemDto(item));
        Mockito
                .when(commentRepository.save(COMMENT))
                .thenReturn(COMMENT);
        Mockito
                .when(userService.getUser(1L))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        CommentDto commentDto = commentService.createComment(1L, 1L, COMMENT);

        assertEquals(1, commentDto.getId());
        assertEquals("Ivan", commentDto.getAuthorName());
        assertEquals("Какой-то текст", commentDto.getText());
    }

    @Test
    void whenCommentNotEqualsText_thenReturnException() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("")
                .authorId(1L)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> commentService.createComment(1L, 1L, comment));

        assertEquals("Нет комментария для добавления", exception.getMessage());
    }

    @Test
    void whenBookingIsEmpty_thenReturnException() {
        Mockito
                .when(bookingRepository.getBookingsByItemById(1L))
                .thenReturn(new ArrayList<>());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> commentService.createComment(1L, 1L, COMMENT));

        assertEquals("Пользователь не бронировал вещь", exception.getMessage());
    }

    @Test
    void whenBookingDateStartAfterNow_thenReturnException() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 9, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 9, 1, 13, 10, 10))
                .bookerId(1L)
                .build();

        Mockito
                .when(bookingRepository.getBookingsByItemById(1L))
                .thenReturn(List.of(booking));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> commentService.createComment(1L, 1L, COMMENT));

        assertEquals("Отзыв нельзя оставить до начала пользования вещью", exception.getMessage());
    }
}