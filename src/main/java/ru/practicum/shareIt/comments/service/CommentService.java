package ru.practicum.shareIt.comments.service;

import ru.practicum.shareIt.comments.entity.Comment;
import ru.practicum.shareIt.comments.entity.CommentDto;

public interface CommentService {

    CommentDto createComment (long userId, long itemId, Comment comment);
}
