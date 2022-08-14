package ru.practicum.shareIt.comments;

public interface CommentService {

    CommentDto createComment (long userId, long itemId, Comment comment);
}
