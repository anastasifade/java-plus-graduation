package ru.practicum.main.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

@FeignClient(name = "stats-server")
public interface StatsClient {

    @GetMapping("/stats")
    List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
                             @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
                             @RequestParam List<String> uris,
                             @RequestParam Boolean unique);

    @PostMapping("/hit")
    void saveStat(@RequestBody EndpointHit hit);
}
