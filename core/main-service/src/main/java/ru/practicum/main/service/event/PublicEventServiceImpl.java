package ru.practicum.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.client.StatsClientOrchestrator;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.enums.EventSort;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClientOrchestrator statsClient;

    @Override
    public List<EventShortDto> getAllEvents(EventSearchParams params) {
        log.info("PublicEventService: request for events with params: {}.", params);

        List<Event> events = getPublishedEvents(params);

        if (params.text() != null && !params.text().isBlank()) {
            events = filterByText(events, params.text().toLowerCase().trim());
        }

        if (params.paid() != null) {
            events = filterByPaid(events, params.paid());
        }

        if (params.onlyAvailable()) {
            events = filterAvailable(events);
        }

        Map<Long, EventStatistics> statsMap = getEventStatistics(events);

        List<EventShortDto> result = events.stream()
                .map(event -> EventMapper.toShortDto(event, statsMap.getOrDefault(event.getId(),
                        new EventStatistics(0, 0))))
                .collect(Collectors.toList());

        if (EventSort.VIEWS.equals(params.sort())) {
            result.sort((e1, e2) -> Long.compare(e2.views(), e1.views()));
        }

        return result;
    }

    private List<Event> getPublishedEvents(EventSearchParams params) {
        int from = params.from();
        int size = params.size();

        LocalDateTime start = params.rangeStart();
        LocalDateTime end = params.rangeEnd();
        if (start == null && end == null) {
            start = LocalDateTime.now();
        }

        List<Long> categories = params.categories() == null || params.categories().isEmpty() ?
                List.of(-1L) : params.categories();

        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (EventSort.EVENT_DATE.equals(params.sort())) {
            pageRequest = pageRequest.withSort(Sort.by("eventDate").ascending());
        }

        return eventRepository.findPublishedEvents(
                categories,
                params.categories() == null || params.categories().isEmpty(),
                start,
                end,
                pageRequest
        );
    }

    private List<Event> filterByText(List<Event> events, String text) {
        return events.stream()
                .filter(event ->
                        event.getAnnotation().toLowerCase().contains(text) ||
                                event.getDescription().toLowerCase().contains(text)
                )
                .collect(Collectors.toList());
    }

    private List<Event> filterByPaid(List<Event> events, boolean paid) {
        return events.stream()
                .filter(event -> event.getPaid().equals(paid))
                .collect(Collectors.toList());
    }

    // TODO: fix N+1 problem
    private List<Event> filterAvailable(List<Event> events) {
        return events.stream()
                .filter(event -> {
                    if (event.getParticipantLimit() == 0) return true;
                    long confirmed = participationRequestRepository
                            .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                    return confirmed < event.getParticipantLimit();
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long id) {
        log.info("PublicEventService: request for event by id={}", id);

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("No published events with id=%d found.", id)));

        Map<Long, EventStatistics> statsMap = getEventStatistics(List.of(event));
        EventStatistics stats = statsMap.getOrDefault(id, new EventStatistics(0, 0));

        return EventMapper.toFullDto(event, stats);
    }

    // TODO: deduplicate
    private Map<Long, EventStatistics> getEventStatistics(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        try {
            Map<Long, Long> confirmedRequestsMap = events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> participationRequestRepository
                                    .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                    ));

            List<String> uris = events.stream()
                    .map(event -> "/events/" + event.getId())
                    .collect(Collectors.toList());

            log.debug("=== GETTING STATS FOR URIS: {}", uris);

            List<ViewStats> viewStats = statsClient.getStats(uris, true);

            log.debug("=== VIEW STATS RESPONSE: {}", viewStats);

            Map<Long, Long> viewsMap = Map.of();
            if (viewStats != null && !viewStats.isEmpty()) {
                viewsMap = viewStats.stream()
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.uri()),
                                ViewStats::hits
                        ));
            }

            log.debug("=== VIEWS MAP: {}", viewsMap);

            Map<Long, Long> finalViewsMap = viewsMap;
            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(
                                    confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                                    finalViewsMap.getOrDefault(event.getId(), 0L)
                            )
                    ));

        } catch (Exception e) {
            log.error("Error when requesting event statistics", e);
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