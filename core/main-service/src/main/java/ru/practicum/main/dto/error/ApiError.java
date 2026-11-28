package ru.practicum.main.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constants.DtoConstants.DATE_TIME_PATTERN;

public record ApiError(
        List<Object> errors,
        String message,
        String reason,
        HttpStatus status,
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime timestamp
) {
    public ApiError(
            List<Object> errors,
            String message,
            String reason,
            HttpStatus status
    ) {
        this(errors, message, reason, status, LocalDateTime.now());
    }
}