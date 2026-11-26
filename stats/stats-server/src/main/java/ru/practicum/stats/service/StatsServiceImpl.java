package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.StatsRequest;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final HitRepository repository;

    @Override
    public List<ViewStats> getStats(StatsRequest request) {
        log.debug("Request to get stats with parameters: {}.", request);
        return request.unique() ?
                getStatsUniqueIp(request.start(), request.end(), request.uris()) :
                getStatsNonUniqueIp(request.start(), request.end(), request.uris());
    }

    private List<ViewStats> getStatsUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return repository.getHitsByDateAndUniqueIpLimits(start, end, uris);
    }

    private List<ViewStats> getStatsNonUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return repository.getHitsByDateLimits(start, end, uris);
    }

    @Override
    @Transactional
    public void saveHit(EndpointHit endpointHit) {
        log.debug("Request to save endpoint hit: {}.", endpointHit);

        Hit hit = HitMapper.toHit(endpointHit);
        repository.save(hit);

        log.info("Endpoint hit saved successfully, id: {}.", hit.getId());
    }
}
