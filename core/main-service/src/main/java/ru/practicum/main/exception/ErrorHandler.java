package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.dto.error.ApiError;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.warn("Not found: {}", e.getMessage());
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(), "Object not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        log.warn("Conflict: {}", e.getMessage());
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(), "Conflict", HttpStatus.CONFLICT);

    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException e) {
        log.warn("Bad request: {}", e.getMessage());
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(), "Bad request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(),
                "Invalid request data", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleAuthFailure(ForbiddenException e) {
        log.warn("Authorization failed: {}.", e.getMessage());
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(),
                "Authorization failure", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return new ApiError(List.of(e.getStackTrace()), e.getMessage(),
                "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}