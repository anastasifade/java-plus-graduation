package ru.practicum.main.dto.request;

import java.util.Set;

public record EventRequestStatusUpdateResult(
        Set<ParticipationRequestDto> confirmedRequests,
        Set<ParticipationRequestDto> rejectedRequests
) {
}
