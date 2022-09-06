package ru.practicum.shareit.booking.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.entity.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;
    private Long bookerId;
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
