package ru.practicum.main.controller.publicapi;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.service.subscription.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;
import static ru.practicum.main.constants.HeaderConstants.X_USER_ID;

@Loggable
@Validated
@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@PathVariable(name = "userId") Long userId,
                          @PathVariable(name = "authorId") Long authorId,
                          @RequestHeader(X_USER_ID) Long requesterId) {
        authorize(requesterId, userId);
        subscriptionService.subscribe(userId, authorId);
    }

    @DeleteMapping("/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable(name = "userId") Long userId,
                            @PathVariable(name = "authorId") Long authorId,
                            @RequestHeader(X_USER_ID) Long requesterId) {
        authorize(requesterId, userId);
        subscriptionService.unsubscribe(userId, authorId);
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionDto> getSubscriptions(@PathVariable(name = "userId") Long userId,
                                                  @RequestHeader(X_USER_ID) Long requesterId) {
        authorize(requesterId, userId);
        return subscriptionService.getSubscriptions(userId);
    }

    @GetMapping("/subscribers")
    public List<SubscriptionDto> getSubscribers(@PathVariable(name = "userId") Long userId,
                                                @RequestHeader(X_USER_ID) Long requesterId) {
        authorize(requesterId, userId);
        return subscriptionService.getSubscribers(userId);
    }

    @GetMapping("/subscriptions/events")
    public List<EventShortDto> getEventsFromSubscriptions(@PathVariable(name = "userId") Long userId,

                                                          @RequestHeader(X_USER_ID) Long requesterId,

                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                          LocalDateTime start,

                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                          LocalDateTime end,

                                                          @RequestParam(defaultValue = "0")
                                                          @PositiveOrZero Integer from,

                                                          @RequestParam(defaultValue = "10")
                                                          @Positive Integer size
    ) {

        authorize(requesterId, userId);
        EventSearchParams params = new EventSearchParams(null, null, null, start, end,
                null, null, from, size);
        return subscriptionService.getEventsFromSubscriptions(userId, params);
    }

    private void authorize(Long requesterId, Long targetId) {
        if (!targetId.equals(requesterId)) {
            throw new ForbiddenException(String.format("User [id=%d] unauthorized to access subscriptions of user[id=%d]",
                    requesterId, targetId));
        }
    }

}
