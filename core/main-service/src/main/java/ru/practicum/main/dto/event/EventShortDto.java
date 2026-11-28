package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

public record EventShortDto(
        Long id,

        String title,
        String annotation,

        CategoryDto category,

        Boolean paid,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,

        UserShortDto initiator,

        Long confirmedRequests,

        Long views
) {

}
