package ru.practicum.stats.exception;

public class InvalidRequestParams extends RuntimeException {
    public InvalidRequestParams(String message) {
        super(message);
    }
}
