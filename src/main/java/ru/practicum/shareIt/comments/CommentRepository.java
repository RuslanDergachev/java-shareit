package ru.practicum.shareIt.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment getCommentById(long commentId);

    List<Comment> getAllByItemId(long itemId);
}
