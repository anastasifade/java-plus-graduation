package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import static ru.practicum.main.constants.DtoConstants.*;

public record NewEventDto(
        @NotBlank(message = "Event title cannot be blank.")
        @Size(min = MIN_EVENT_TITLE, max = MAX_EVENT_TITLE,
                message = "Event title must be within [3-120] symbols.")
        String title,

        @NotBlank(message = "Event annotation cannot be blank.")
        @Size(min = MIN_ANNOTATION, max = MAX_ANNOTATION,
                message = "Event annotation must be within [20-2000] symbols.")
        String annotation,

        @NotBlank(message = "Event description cannot be blank.")
        @Size(min = MIN_DESCRIPTION, max = MAX_DESCRIPTION,
                message = "Event description must be within [20-7000] symbols.")
        String description,

        @NotNull(message = "Category identifier is required.")
        Long category,

        @NotNull(message = "Event date is required.")
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,

        @Valid
        @NotNull(message = "Event location coordinates are required.")
        LocationDto location,

        boolean paid,

        @PositiveOrZero(message = "Participation limit cannot be null.")
        int participantLimit,

        Boolean requestModeration
) {

}
