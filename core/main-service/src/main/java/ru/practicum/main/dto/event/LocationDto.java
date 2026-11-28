package ru.practicum.main.dto.event;

import jakarta.validation.constraints.NotNull;

public record LocationDto(
        @NotNull(message = "Latitude cannot be null.")
        Double lat,
        @NotNull(message = "Longitude cannot be null.")
        Double lon
) {

}
