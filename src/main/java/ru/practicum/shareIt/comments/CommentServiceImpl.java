package ru.practicum.shareIt.comments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.booking.Booking;
import ru.practicum.shareIt.booking.BookingRepository;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.ItemDto;
import ru.practicum.shareIt.item.ItemMapper;
import ru.practicum.shareIt.item.ItemService;
import ru.practicum.shareIt.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    UserService userService;
    ItemService itemService;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto createComment(long userId, long itemId, Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new ValidationException("Нет комментария для добавления");
        }
        List<Booking> bookingUser = bookingRepository.getBookingsByItemById(itemId).stream()
                .filter(booking -> booking.getBookerId() == userId)
                .sorted(comparing(Booking::getStart)).collect(Collectors.toList());
        if (bookingUser.size() == 0) {
            throw new NotFoundException("Пользователь не бронировал вещь");
        }
        for (Booking booking : bookingUser) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                throw new ValidationException("Отзыв нельзя оставить до начала пользования вещью");
            } else {
                break;
            }
        }
        ItemDto itemDto = itemService.getItemById(userId, itemId);
        comment.setAuthorId(userId);
        comment.setItem(ItemMapper.toItem(itemDto.getOwnerId(), itemDto));
        comment.setCreated(LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setAuthorName(userService.getUser(userId).getName());
        return commentDto;
    }
}
