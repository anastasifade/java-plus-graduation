package ru.practicum.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.main.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

public record ParticipationRequestDto(
        Long id,
        Long event,
        Long requester,
        RequestStatus status,
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime created
) {

}
