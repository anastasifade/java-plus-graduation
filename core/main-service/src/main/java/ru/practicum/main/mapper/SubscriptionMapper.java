package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.model.Subscription;
import ru.practicum.main.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class SubscriptionMapper {

    public SubscriptionDto toSubscriptionDto(Subscription subscription,
                                                    UserShortDto author, UserShortDto subscriber) {
        return new SubscriptionDto(subscription.getId(),
                subscriber,
                author,
                subscription.getCreatedAt()
        );
    }

    public Subscription toSubscription(User subscriber, User author) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setAuthor(author);
        subscription.setCreatedAt(LocalDateTime.now());
        return subscription;
    }

}