package ru.practicum.shareIt.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareIt.comments.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment getCommentById(long commentId);

    List<Comment> getAllByItemId(long itemId);
}
