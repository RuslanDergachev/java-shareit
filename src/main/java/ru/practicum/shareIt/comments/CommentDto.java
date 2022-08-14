package ru.practicum.shareIt.comments;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
