package ru.practicum.main.dto.event;

import ru.practicum.main.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

public record EventSearchParams(
        String text,
        List<Long> categories,
        Boolean paid,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Boolean onlyAvailable,
        EventSort sort,
        Integer from,
        Integer size
) {
}
