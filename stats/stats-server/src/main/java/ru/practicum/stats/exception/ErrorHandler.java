package ru.practicum.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.dto.StatsError;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatsError handle(InvalidRequestParams e) {
        log.warn("Invalid request parameters: {}.", e.getMessage());
        return new StatsError(List.of(e.getStackTrace()), e.getMessage(), "Invalid request",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StatsError handle(Throwable e) {
        log.error("Unexpected error", e);
        return new StatsError(List.of(e.getStackTrace()), e.getMessage(),
                "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
