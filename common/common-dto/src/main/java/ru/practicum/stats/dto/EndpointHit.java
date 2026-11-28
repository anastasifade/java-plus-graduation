package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EndpointHit(
        @NotBlank(message = "Service identifier (application name) cannot be blank.")
        String app,
        @NotBlank(message = "Endpoint URI cannot be blank.")
        String uri,
        @NotBlank(message = "User's IP-address cannot be blank.")
        String ip,
        @NotNull(message = "Timestamp cannot be null.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
}