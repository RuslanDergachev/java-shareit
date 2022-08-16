package ru.practicum.shareIt.user.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Email;


@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @Email
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "email", unique = true)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
