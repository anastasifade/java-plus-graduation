package ru.practicum.main.dto.subscription;

import ru.practicum.main.dto.user.UserShortDto;

import java.time.LocalDateTime;

public record SubscriptionDto(
        Long id,
        UserShortDto subscriber,
        UserShortDto author,
        LocalDateTime createdAt
) {
}
