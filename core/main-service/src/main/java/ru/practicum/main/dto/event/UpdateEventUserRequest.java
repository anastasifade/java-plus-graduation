package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.main.enums.EventStateUserAction;
import ru.practicum.main.validation.NullOrNotBlank;

import java.time.LocalDateTime;

import static ru.practicum.main.constants.DtoConstants.*;

public record UpdateEventUserRequest(
        @NullOrNotBlank(message = "Event title cannot be blank.")
        @Size(min = MIN_EVENT_TITLE, max = MAX_EVENT_TITLE, message = "Event title must be within [3-120] symbols.")
        String title,

        @NullOrNotBlank(message = "Event annotation cannot be blank.")
        @Size(min = MIN_ANNOTATION, max = MAX_ANNOTATION,
                message = "Event annotation must be within [20-2000] symbols.")
        String annotation,

        @NullOrNotBlank(message = "Event description cannot be blank.")
        @Size(min = MIN_DESCRIPTION, max = MAX_DESCRIPTION,
                message = "Event description must be within [20-7000] symbols.")
        String description,

        Long category,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,

        @Valid
        LocationDto location,

        Boolean paid,

        @PositiveOrZero(message = "Participation limit cannot be null.")
        Integer participantLimit,

        Boolean requestModeration,

        EventStateUserAction stateAction
) {

}
