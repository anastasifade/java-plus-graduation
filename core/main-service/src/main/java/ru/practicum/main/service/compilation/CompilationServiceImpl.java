package ru.practicum.main.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.client.StatsClientOrchestrator;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.stats.dto.ViewStats;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsClientOrchestrator statsClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        log.debug("Creating compilation: title={}", dto.title());

        Set<Event> events = new HashSet<>();
        if (dto.events() != null && !dto.events().isEmpty()) {
            events.addAll(eventRepository.findAllById(dto.events()));
        }

        Compilation compilation = CompilationMapper.toEntity(dto, events);
        compilation = compilationRepository.save(compilation);

        log.info("Compilation created: id={}, title={}", compilation.getId(), compilation.getTitle());

        Map<Long, EventStatistics> statsMap = getStatisticsForEvents(compilation.getEvents());
        return CompilationMapper.toDto(compilation, statsMap);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("Deleting compilation id={}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " not found");
        }

        compilationRepository.deleteById(compId);
        log.info("Compilation deleted: id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        log.debug("Updating compilation id={}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));

        Set<Event> events = null;
        if (dto.events() != null) {
            events = new HashSet<>(eventRepository.findAllById(dto.events()));
        }

        CompilationMapper.updateEntity(dto, events, compilation);
        compilation = compilationRepository.save(compilation);

        log.info("Compilation updated: id={}", compilation.getId());

        Map<Long, EventStatistics> statsMap = getStatisticsForEvents(compilation.getEvents());
        return CompilationMapper.toDto(compilation, statsMap);
    }

    // TODO: refactor with OpenFeign client; deduplicate logic (similar method in EventServiceImpl)

    private Map<Long, EventStatistics> getStatisticsForEvents(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        try {
            List<String> uris = events.stream()
                    .map(event -> "/events/" + event.getId())
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

            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(0L,
                                    viewsMap.getOrDefault(event.getId(), 0L))
                    ));

        } catch (Exception e) {
            log.error("Error when requesting event statistics.", e);
            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(0L, 0L)
                    ));
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}