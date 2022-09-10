package ru.practicum.shareit.item.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Data
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    private Long ownerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
}
