package ru.practicum.main.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.event.EventAdminSearchParams;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

@Loggable
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,

                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                        LocalDateTime rangeStart,

                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                        LocalDateTime rangeEnd,

                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }

        EventAdminSearchParams params = new EventAdminSearchParams(users, states, categories, rangeStart, rangeEnd,
                from, size);
        return eventService.getEventsWithParameters(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEvent(eventId, updateEventAdminRequest);
    }

}
