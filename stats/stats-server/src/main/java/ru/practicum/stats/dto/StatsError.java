package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record StatsError(
        List<Object> errors,
        String message,
        String reason,
        HttpStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    public StatsError(
            List<Object> errors,
            String message,
            String reason,
            HttpStatus status
    ) {
        this(errors, message, reason, status, LocalDateTime.now());
    }
}