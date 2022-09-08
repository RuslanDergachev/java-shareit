package ru.practicum.shareit.booking.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class BookingDto {
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BookingStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long bookerId;
    private Long itemId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String itemName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime start;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime end;
}
