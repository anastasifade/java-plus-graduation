package ru.practicum.main.dto.compilation;

import jakarta.validation.constraints.Size;
import ru.practicum.main.validation.NullOrNotBlank;

import java.util.Set;

import static ru.practicum.main.constants.DtoConstants.MAX_TITLE;

public record UpdateCompilationRequest(
        Set<Long> events,

        Boolean pinned,

        @NullOrNotBlank(message = "Cannot change compilation title to a blank value.")
        @Size(max = MAX_TITLE, message = "Compilation title cannot be over 50 symbols.")
        String title
) {

}
