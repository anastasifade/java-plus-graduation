package ru.practicum.stats.service;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.StatsRequest;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(StatsRequest request);

    void saveHit(EndpointHit hit);
}
