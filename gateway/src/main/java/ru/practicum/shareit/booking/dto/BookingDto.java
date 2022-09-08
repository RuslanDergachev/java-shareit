package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
