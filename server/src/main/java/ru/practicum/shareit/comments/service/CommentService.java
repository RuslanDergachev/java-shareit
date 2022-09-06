package ru.practicum.shareit.comments.service;

import ru.practicum.shareit.comments.entity.Comment;
import ru.practicum.shareit.comments.entity.CommentDto;

public interface CommentService {

    CommentDto createComment (long userId, long itemId, Comment comment);
}
