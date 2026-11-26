package ru.practicum.main.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;


@FeignClient(name = "stats-server")
public interface StatsClient {

    @GetMapping("/stats")
    List<ViewStats> getStats(@RequestParam LocalDateTime start,
                             @RequestParam LocalDateTime end,
                             @RequestParam List<String> uris,
                             @RequestParam Boolean unique);

    @PostMapping("/hit")
    void saveStat(@RequestBody EndpointHit hit);
}
