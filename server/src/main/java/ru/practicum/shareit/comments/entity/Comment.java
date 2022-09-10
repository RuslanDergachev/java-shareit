package ru.practicum.shareit.comments.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.entity.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Data
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;
    private Long authorId;
    private LocalDateTime created;
}
