package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.StatsRequest;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.StatsService;
import ru.practicum.common.logging.Loggable;

import java.time.LocalDateTime;
import java.util.List;

@Loggable
@RestController
@RequiredArgsConstructor
public class StatsController {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final StatsService statsService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(
            @RequestParam
            @DateTimeFormat(pattern = PATTERN)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(pattern = PATTERN)
            LocalDateTime end,

            @RequestParam(required = false)
            List<String> uris,

            @RequestParam(defaultValue = "false")
            boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        StatsRequest request = new StatsRequest(start, end, uris, unique);
        return statsService.getStats(request);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStat(@Valid @RequestBody EndpointHit endpointHit) {
        statsService.saveHit(endpointHit);
    }
}
