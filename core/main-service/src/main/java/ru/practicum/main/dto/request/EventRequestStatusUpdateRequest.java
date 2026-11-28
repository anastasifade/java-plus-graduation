package ru.practicum.main.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.validation.ValidUpdateRequestStatus;

import java.util.Set;

public record EventRequestStatusUpdateRequest(
        @NotEmpty(message = "At least one participation request must be provided.")
        Set<@NotNull(message = "Participation request identifier cannot be null.")
                Long> requestIds,

        @ValidUpdateRequestStatus
        RequestStatus status
) {
}
