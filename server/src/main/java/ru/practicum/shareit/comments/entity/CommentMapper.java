package ru.practicum.shareit.comments.entity;

import ru.practicum.shareit.comments.entity.Comment;
import ru.practicum.shareit.comments.entity.CommentDto;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(null)
                .created(comment.getCreated())
                .build();
    }
}
