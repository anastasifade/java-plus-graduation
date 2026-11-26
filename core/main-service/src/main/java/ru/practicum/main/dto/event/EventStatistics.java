package ru.practicum.main.dto.event;

public record EventStatistics(
        long confirmedRequests,
        long views
) {
}
