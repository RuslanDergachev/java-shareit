package ru.practicum.shareIt.item.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareIt.booking.entity.LastBooking;
import ru.practicum.shareIt.booking.entity.NextBooking;
import ru.practicum.shareIt.comments.entity.CommentDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long ownerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime start;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime end;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

}
