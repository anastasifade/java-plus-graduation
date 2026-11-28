package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.enums.EventState;

import java.time.LocalDateTime;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

public record EventFullDto(
        Long id,

        String title,
        String annotation,
        String description,

        CategoryDto category,

        Boolean paid,

        EventState state,

        LocationDto location,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime createdOn,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime publishedOn,

        UserShortDto initiator,

        Integer participantLimit,

        Long confirmedRequests,

        Long views,

        Boolean requestModeration
) {

}
