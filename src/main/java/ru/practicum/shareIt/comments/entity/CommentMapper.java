package ru.practicum.shareIt.comments.entity;

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
