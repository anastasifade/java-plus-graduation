package ru.practicum.main.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids) {
        log.debug("main-service - UserServiceImpl: Request to view users with ids in: {}.", ids);

        return repository.findAllById(ids)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getUsers(int from, int size) {
        log.debug("main-service - UserServiceImpl: Request to view users. From: {}. Size: {}.", from, size);

        return repository.findAll(from, size)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest dto) {
        log.debug("main-service - UserServiceImpl: Request to create user.");

        if (repository.existsByEmailIgnoreCase(dto.email().trim())) {
            log.warn("main-service - UserServiceImpl: Failed to create user. Email already exists.");
            throw new ConflictException(String.format("Email [%s] already exists.", dto.email()));
        }

        User user = UserMapper.toEntity(dto);
        user = repository.save(user);

        log.info("main-service - UserServiceImpl: User [id={}] created.", user.getId());

        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        log.debug("main-service - UserServiceImpl: Request to delete user [id={}].", id);

        if (!repository.existsById(id)) {
            log.warn("main-service - UserServiceImpl: User [id={}] not found.", id);
            throw new NotFoundException(String.format("User [id=%d] not found.", id));
        }

        repository.deleteById(id);

        log.info("User [id={}] removed.", id);
    }
}
