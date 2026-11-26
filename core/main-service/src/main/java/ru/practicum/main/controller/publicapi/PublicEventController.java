package ru.practicum.main.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.client.StatsClientOrchestrator;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.enums.EventSort;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.service.event.PublicEventService;
import ru.practicum.main.service.subscription.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;
import static ru.practicum.main.constants.HeaderConstants.X_USER_ID;

@Loggable
@Validated
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicEventService publicEventService;
    private final SubscriptionService subscriptionService;
    private final StatsClientOrchestrator statsClientOrchestrator;


    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestHeader(value = X_USER_ID, required = false) Long userId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "false") Boolean subscriptions,
            HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }
        saveHit(request);

        EventSearchParams params = new EventSearchParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        return subscriptions ?
                getEventsBySubscription(params, userId) :
                publicEventService.getAllEvents(params);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable Long id,
            HttpServletRequest request) {
        saveHit(request);
        return publicEventService.getEventById(id);
    }

    private List<EventShortDto> getEventsBySubscription(EventSearchParams params, Long userId) {
        if (userId == null) {
            throw new ForbiddenException("User ID cannot be null when searching for subscription events.");
        }
        return subscriptionService.getEventsFromSubscriptions(userId, params);
    }

    private void saveHit(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();

            statsClientOrchestrator.saveHit(
                    "main-service",
                    uri,
                    ip
            );
        } catch (Exception ignored) {
            // error logged in stats client orchestrator
        }
    }
}