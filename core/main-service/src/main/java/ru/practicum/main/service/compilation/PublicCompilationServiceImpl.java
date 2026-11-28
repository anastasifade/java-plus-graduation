package ru.practicum.main.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.client.StatsClientOrchestrator;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final StatsClientOrchestrator statsClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        Set<Long> eventIds = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, EventStatistics> statsMap = getEventStatistics(eventIds);

        return compilations.stream()
                .map(compilation -> CompilationMapper.toDto(compilation, statsMap))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Подборки с id=%d не существует", id)));

        Set<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, EventStatistics> statsMap = getEventStatistics(eventIds);

        return CompilationMapper.toDto(compilation, statsMap);
    }

    // TODO: deduplicate logic
    private Map<Long, EventStatistics> getEventStatistics(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            List<ViewStats> viewStats = statsClient.getStats(uris);

            if (viewStats == null) {
                return Map.of();
            }

            Map<Long, Long> viewsMap = viewStats.stream()
                    .collect(Collectors.toMap(
                            stat -> extractEventIdFromUri(stat.uri()),
                            ViewStats::hits
                    ));

            return eventIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> new EventStatistics(0L, viewsMap.getOrDefault(id, 0L))
                    ));

        } catch (Exception e) {
            log.error("Error when requesting event statistics", e);
            return eventIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> new EventStatistics(0L, 0L)
                    ));
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}