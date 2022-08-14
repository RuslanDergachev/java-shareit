package ru.practicum.shareIt.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareIt.booking.LastBooking;
import ru.practicum.shareIt.booking.NextBooking;
import ru.practicum.shareIt.comments.CommentDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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

}
