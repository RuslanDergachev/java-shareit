package ru.practicum.shareIt.request;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
@DynamicUpdate
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String description;
    @Column(name = "requestor_id")
    long requestor;
}
