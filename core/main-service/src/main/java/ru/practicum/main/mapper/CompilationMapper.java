package ru.practicum.main.mapper;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toDto(Compilation compilation, Map<Long, EventStatistics> stats) {
        Set<EventShortDto> events = compilation.getEvents()
                .stream()
                .map(event -> EventMapper.toShortDto(
                        event,
                        stats.getOrDefault(
                                event.getId(),
                                new EventStatistics(0, 0)
                        )))
                .collect(Collectors.toSet());

        return new CompilationDto(compilation.getId(),
                compilation.getTitle(),
                events,
                compilation.getPinned());
    }


    public Compilation toEntity(NewCompilationDto dto,
                                Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.title());
        compilation.setPinned(dto.pinned());
        compilation.setEvents(events);
        return compilation;
    }


    public void updateEntity(UpdateCompilationRequest dto,
                             @Nullable Set<Event> events,
                             Compilation compilation) {
        if (dto.title() != null) {
            compilation.setTitle(dto.title().trim());
        }

        if (dto.pinned() != null) {
            compilation.setPinned(dto.pinned());
        }

        if (events != null) {
            compilation.setEvents(events);
        }
    }
}