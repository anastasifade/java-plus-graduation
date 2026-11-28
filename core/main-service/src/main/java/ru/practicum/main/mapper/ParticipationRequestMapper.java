package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(request.getId(),
                request.getEvent().getId(),
                request.getRequestor().getId(),
                request.getStatus(),
                request.getCreated());
    }

    public ParticipationRequest toRequest(User user, Event event) {
        RequestStatus status = (!event.getRequestModeration() || event.getParticipantLimit() == 0) ?
                RequestStatus.CONFIRMED : RequestStatus.PENDING;

        ParticipationRequest request = new ParticipationRequest();

        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);
        request.setEvent(event);
        request.setStatus(status);

        return request;
    }
}
