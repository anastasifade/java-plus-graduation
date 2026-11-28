package ru.practicum.main.dto.compilation;

import ru.practicum.main.dto.event.EventShortDto;

import java.util.Set;

public record CompilationDto(
        Long id,
        String title,
        Set<EventShortDto> events,
        Boolean pinned
) {
}
