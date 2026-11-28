package ru.practicum.stats.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatsRequest(
        LocalDateTime start,
        LocalDateTime end,
        List<String> uris,
        boolean unique
) {
}
