package ru.practicum.main.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.main.exception.StatsClientException;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClientOrchestrator {

    private final StatsClient client;

    public List<ViewStats> getStats(List<String> uris) {
        return getStats(uris, false);
    }

    public List<ViewStats> getStats(List<String> uris, boolean unique) {
        return getStats(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                LocalDateTime.now().plusDays(1),
                uris,
                unique);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
            return client.getStats(start, end, uris, unique);
        } catch (Exception e) {
            log.warn("Error when requesting to get stats. Message: {}. Cause: {}.", e.getMessage(),
                    e.getCause().getMessage());
            throw new StatsClientException("Failed to read stats.");
        }
    }

    public void saveHit(String app, String uri, String ip) {
        EndpointHit hit = new EndpointHit(app, uri, ip, LocalDateTime.now());
        try {
            client.saveStat(hit);
        } catch (Exception e) {
            log.warn("Error when requesting to save hit. Message: {}. Cause: {}.", e.getMessage(),
                    e.getCause().getMessage());
            throw new StatsClientException("Failed to save endpoint hit.");
        }
    }

}
