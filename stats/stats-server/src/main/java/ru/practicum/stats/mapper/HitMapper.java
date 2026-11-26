package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.model.Hit;

@UtilityClass
public class HitMapper {
    public Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.app().trim())
                .ip(endpointHit.ip().trim())
                .uri(endpointHit.uri().trim())
                .timestamp(endpointHit.timestamp())
                .build();
    }
}
