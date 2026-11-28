package ru.practicum.main.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

import static ru.practicum.main.constants.DtoConstants.MAX_TITLE;

public record NewCompilationDto(
        @NotBlank(message = "Compilation title cannot be blank.")
        @Size(max = MAX_TITLE, message = "Compilation title cannot be over 50 symbols.")
        String title,

        Set<@NotNull(message = "Event identifier cannot be null.") Long> events,

        boolean pinned
) {
}
