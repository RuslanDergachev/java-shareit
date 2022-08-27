package ru.practicum.shareIt.request.model.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
@DynamicUpdate
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String description;
    @Column(name = "requestor_id")
    Long requestorId;
}
