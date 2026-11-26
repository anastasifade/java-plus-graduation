package ru.practicum.main.service.event;

import ru.practicum.main.dto.event.*;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventFullDto> getEventsWithParameters(EventAdminSearchParams params);
}