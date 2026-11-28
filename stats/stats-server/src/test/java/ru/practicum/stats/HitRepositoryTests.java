package ru.practicum.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class HitRepositoryTests {

    @Autowired
    private HitRepository hitRepository;

    private Hit hit;

    @BeforeEach
    void setUp() {
        hit = Hit.builder()
                .app("test-app")
                .uri("/test")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void test_correctlySavesHit_andAssignsId() {
        hitRepository.save(hit);
        assertNotNull(hit.getId());
    }

    @Test
    void testFindAll() {
        hitRepository.save(hit);

        List<Hit> hits = hitRepository.findAll();

        assertNotEquals(0, hits.size());
        assertEquals(hit, hits.getFirst());
    }
}