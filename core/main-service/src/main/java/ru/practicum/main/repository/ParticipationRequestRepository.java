package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.model.ParticipationRequest;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequestorId(Long userId);

    boolean existsByRequestorIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByIdAndRequestorId(Long requestId, Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);
}