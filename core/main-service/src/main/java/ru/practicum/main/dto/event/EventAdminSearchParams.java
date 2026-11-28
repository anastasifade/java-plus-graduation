package ru.practicum.main.dto.event;

import ru.practicum.main.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public record EventAdminSearchParams(
        List<Long> userIds,
        List<EventState> states,
        List<Long> categoryIds,
        LocalDateTime start,
        LocalDateTime end,
        Integer from,
        Integer size
) {
}
