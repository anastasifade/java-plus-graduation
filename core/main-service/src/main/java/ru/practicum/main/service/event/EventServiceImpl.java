package ru.practicum.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.client.StatsClientOrchestrator;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClientOrchestrator statsClient;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        log.debug("Getting events for user id={}, from={}, size={}", userId, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        Map<Long, EventStatistics> statsMap = getEventStatistics(events);

        return events.stream()
                .map(event -> EventMapper.toShortDto(event, statsMap.getOrDefault(event.getId(),
                        new EventStatistics(0, 0))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.debug("Creating event for user id={}", userId);

        User user = getUserById(userId);
        Category category = getCategoryById(newEventDto.category());

        if (newEventDto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event cannot be created on a notice shorter than 2 hours.");
        }

        Event event = EventMapper.toEntity(newEventDto, category, user);
        event = eventRepository.save(event);

        log.info("Event created: id={}, title={}", event.getId(), event.getTitle());

        return EventMapper.toFullDto(event, getEventStatistics(List.of(event)).getOrDefault(event.getId(),
                new EventStatistics(0, 0)));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.debug("Getting event id={} for user id={}", eventId, userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        return EventMapper.toFullDto(event, getEventStatistics(List.of(event)).getOrDefault(eventId,
                new EventStatistics(0, 0)));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.debug("Updating event id={} by user id={}", eventId, userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.eventDate() != null &&
                request.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours from now");
        }

        Category category = request.category() == null ? null : getCategoryById(request.category());

        EventMapper.updateEntity(request, event, category);

        switch (request.stateAction()) {
            case null -> {}
            case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
        }

        event = eventRepository.save(event);

        log.info("Event updated: id={}, state={}", event.getId(), event.getState());
        return EventMapper.toFullDto(event, getEventStatistics(List.of(event))
                .getOrDefault(eventId, new EventStatistics(0, 0)));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        log.debug("Updating event id={}, access level: admin.", eventId);

        Event event = getEventById(eventId);

        Category category = request.category() == null ? null : getCategoryById(request.category());
        if (request.eventDate() != null &&
                request.eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Event date can be updated no later than 1 hour in advance.");
        }

        EventMapper.updateEntity(event, request, category);
        switch (request.stateAction()) {
            case null -> {}
            case PUBLISH_EVENT -> publish(event);
            case REJECT_EVENT -> reject(event);
        }

        event = eventRepository.save(event);

        log.info("Event updated [access: admin]: id={}, state={}", event.getId(), event.getState());

        return EventMapper.toFullDto(event, getEventStatistics(List.of(event))
                .getOrDefault(eventId, new EventStatistics(0, 0)));
    }

    @Override
    public List<EventFullDto> getEventsWithParameters(EventAdminSearchParams params) {

        log.debug("Getting events with parameters: users={}, states={}, categories={}",
                params.userIds(), params.states(), params.categoryIds());

        int from = params.from();
        int size = params.size();
        Pageable pageable = PageRequest.of(from / size, size);

        List<Long> users = (params.userIds() == null || params.userIds().isEmpty()) ? null : params.userIds();
        List<EventState> states = (params.states() == null || params.states().isEmpty()) ? null : params.states();
        List<Long> categories = (params.categoryIds() == null || params.categoryIds().isEmpty()) ?
                null : params.categoryIds();

        LocalDateTime start = params.start();
        LocalDateTime end = params.end();

        List<Event> events = eventRepository.findEventsWithFilters(
                users,
                users == null,
                states,
                states == null,
                categories,
                categories == null,
                start,
                end,
                pageable
        ).getContent();

        Map<Long, EventStatistics> statsMap = getEventStatistics(events);

        return events.stream()
                .map(event -> EventMapper.toFullDto(
                        event,
                        statsMap.getOrDefault(
                                event.getId(),
                                new EventStatistics(0, 0)
                        )
                ))
                .collect(Collectors.toList());
    }

    // TODO: fix N+1 query problem when collecting confirmed requests
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

            List<ViewStats> viewStats = statsClient.getStats(uris, true);

            Map<Long, Long> viewsMap = Map.of();
            if (viewStats != null && !viewStats.isEmpty()) {
                viewsMap = viewStats.stream()
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.uri()),
                                ViewStats::hits
                        ));
            }

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

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
    }

    private void publish(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Cannot publish event that is not in PENDING state");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
    }

    private void reject(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject already published event");
        }

        event.setState(EventState.CANCELED);
    }
}