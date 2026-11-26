package ru.practicum.main.service.request;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.ParticipationRequestMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        validateUserId(userId);
        return requestRepository.findAllByRequestorId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Creating request for user id={}, event id={}", userId, eventId);

        Event event = getEventById(eventId);

        if (requestRepository.existsByRequestorIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists.");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Cannot request participation in event as event's initiator.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Cannot request participation in an unpublished event.");
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participation limit reached. Cannot request participation.");
        }

        User user = getUserById(userId);
        ParticipationRequest request = ParticipationRequestMapper.toRequest(user, event);
        request = requestRepository.save(request);

        log.info("Request created: id={}, status={}", request.getId(), request.getStatus());

        return ParticipationRequestMapper.toDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Cancelling request id={} for user id={}", requestId, userId);

        ParticipationRequest request = requestRepository.findByIdAndRequestorId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена или не принадлежит пользователю"));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        log.info("Request cancelled: id={}", request.getId());

        return ParticipationRequestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        getEventByIdAndInitiator(eventId, userId);

        return requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest dto) {
        log.info("Changing request status for event id={}, user id={}", eventId, userId);

        User user = getUserById(userId);
        validateEventId(eventId);
        Event event = getEventByIdAndInitiator(eventId, userId);

        if (!event.getInitiator().equals(user)) {
            throw new ValidationException("Not authorized to modify event status.");
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();
        if (dto.status() == RequestStatus.CONFIRMED && limit != 0 && confirmedCount >= limit) {
            throw new ConflictException("Participation limit reached.");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(dto.requestIds());
        if (requests.size() != dto.requestIds().size()) {
            throw new NotFoundException("Could not find some of the requests.");
        }

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Cannot modify request [id=" + request.getId() + "] with status: " +
                        request.getStatus());
            }
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (dto.status() == RequestStatus.CONFIRMED) {
                if (limit == 0 || confirmedCount < limit) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(request);
                    confirmedCount++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(request);
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(request);
            }
        }

        requestRepository.saveAll(confirmed);
        requestRepository.saveAll(rejected);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                confirmed.stream()
                        .map(ParticipationRequestMapper::toDto)
                        .collect(Collectors.toSet()),
                rejected.stream()
                        .map(ParticipationRequestMapper::toDto)
                        .collect(Collectors.toSet())
        );

        log.info("Request status changed: confirmed={}, rejected={}", confirmed.size(), rejected.size());

        return result;
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found.");
        }
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found."));
    }

    private Event getEventByIdAndInitiator(Long eventId, Long initiatorId) {
        return eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " by user with id="
                        + initiatorId + " not found."));
    }
}