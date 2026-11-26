package ru.practicum.main.mapper;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Location;
import ru.practicum.main.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public EventFullDto toFullDto(Event event, EventStatistics stats) {
        LocationDto location = event.getLocation() != null ?
                new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()) : null;

        Long confirmedRequests = stats != null ? stats.confirmedRequests() : 0L;
        Long views = stats != null ? stats.views() : 0L;

        return new EventFullDto(event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                CategoryMapper.toDto(event.getCategory()),
                event.getPaid(),
                event.getState(),
                location,
                event.getEventDate(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                UserMapper.toShortDto(event.getInitiator()),
                event.getParticipantLimit(),
                confirmedRequests,
                views,
                event.getRequestModeration()
        );
    }

    public EventShortDto toShortDto(Event event, EventStatistics stats) {
        Long confirmedRequests = stats != null ? stats.confirmedRequests() : 0L;
        Long views = stats != null ? stats.views() : 0L;

        return new EventShortDto(event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toDto(event.getCategory()),
                event.getPaid(),
                event.getEventDate(),
                UserMapper.toShortDto(event.getInitiator()),
                confirmedRequests,
                views);
    }

    public Event toEntity(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();

        event.setTitle(dto.title().trim());
        event.setAnnotation(dto.annotation().trim());
        event.setDescription(dto.description());
        event.setCategory(category);
        event.setEventDate(dto.eventDate());

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().lat(), dto.location().lon()));
        }

        event.setPaid(dto.paid());
        event.setParticipantLimit(dto.participantLimit());
        event.setRequestModeration(dto.requestModeration() == null || dto.requestModeration());
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setState(EventState.PENDING);

        return event;
    }

    public void updateEntity(Event event, UpdateEventAdminRequest dto, @Nullable Category category) {
        if (dto.title() != null) {
            event.setTitle(dto.title().trim());
        }

        if (dto.annotation() != null) {
            event.setAnnotation(dto.annotation().trim());
        }

        if (dto.description() != null) {
            event.setDescription(dto.description().trim());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().lat(), dto.location().lon()));
        }

        if (dto.paid() != null) {
            event.setPaid(dto.paid());
        }

        if (dto.participantLimit() != null) {
            event.setParticipantLimit(dto.participantLimit());
        }

        if (dto.requestModeration() != null) {
            event.setRequestModeration(dto.requestModeration());
        }
    }

    public void updateEntity(UpdateEventUserRequest dto, Event event, @Nullable Category category) {
        if (dto.title() != null) {
            event.setTitle(dto.title().trim());
        }

        if (dto.annotation() != null) {
            event.setAnnotation(dto.annotation().trim());
        }

        if (dto.description() != null) {
            event.setDescription(dto.description().trim());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().lat(), dto.location().lon()));
        }

        if (dto.paid() != null) {
            event.setPaid(dto.paid());
        }

        if (dto.participantLimit() != null) {
            event.setParticipantLimit(dto.participantLimit());
        }

        if (dto.requestModeration() != null) {
            event.setRequestModeration(dto.requestModeration());
        }
    }
}