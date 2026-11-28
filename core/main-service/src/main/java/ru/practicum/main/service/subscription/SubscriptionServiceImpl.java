package ru.practicum.main.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.SubscriptionMapper;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.Subscription;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.SubscriptionRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.event.PublicEventService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PublicEventService eventService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void subscribe(Long subscriberId, Long authorId) {
        if (subscriberId.equals(authorId)) {
            throw new ConflictException("Cannot subscribe to self..");
        }
        if (subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new ConflictException("Subscription already exists.");
        }

        User subscriber = findById(subscriberId);
        User author = findById(authorId);

        Subscription subscription = SubscriptionMapper.toSubscription(subscriber, author);

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long subscriberId, Long authorId) {
        validateUserId(subscriberId);
        validateUserId(authorId);

        if (!subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new NotFoundException("Subscription not found.");
        }

        subscriptionRepository.deleteBySubscriberIdAndAuthorId(subscriberId, authorId);
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        validateUserId(userId);

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);

        return subscriptions.stream()
                .map(subscription -> SubscriptionMapper.toSubscriptionDto(subscription,
                        UserMapper.toShortDto(subscription.getAuthor()),
                        UserMapper.toShortDto(subscription.getSubscriber())))
                .toList();
    }

    @Override
    public List<SubscriptionDto> getSubscribers(Long userId) {
        validateUserId(userId);

        List<Subscription> subscriptions = subscriptionRepository.findByAuthorId(userId);

        return subscriptions.stream()
                .map(subscription -> SubscriptionMapper.toSubscriptionDto(subscription,
                        UserMapper.toShortDto(subscription.getAuthor()),
                        UserMapper.toShortDto(subscription.getSubscriber())))
                .toList();
    }

    @Override
    public List<EventShortDto> getEventsFromSubscriptions(Long userId, EventSearchParams params) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);
        if (subscriptions.isEmpty()) {
            return List.of();
        }

        List<Long> authorIds = subscriptions.stream()
                .map(sub -> sub.getAuthor().getId())
                .toList();

        return eventService.getAllEvents(params)
                .stream()
                .filter(event -> authorIds.contains(event.initiator().id()))
                .toList();
    }

    private User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id=" + id + " not found."));
    }

    private void validateUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id=" + id + " not found.");
        }
    }

}
