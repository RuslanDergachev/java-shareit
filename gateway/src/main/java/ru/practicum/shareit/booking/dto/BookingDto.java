package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Getter
@Builder
public class BookingDto {
	private Long id;
	@JsonInclude(NON_NULL)
	private BookingStatus status;
	@JsonInclude(NON_NULL)
	private Long bookerId;
	private Long itemId;
	@JsonInclude(NON_NULL)
	private String itemName;
	@JsonInclude(NON_NULL)
	private LocalDateTime start;
	@JsonInclude(NON_NULL)
	private LocalDateTime end;
}
