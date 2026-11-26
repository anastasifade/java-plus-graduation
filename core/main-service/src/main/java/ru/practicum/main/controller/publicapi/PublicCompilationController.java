package ru.practicum.main.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.service.compilation.PublicCompilationService;

import java.util.List;

@Loggable
@Validated
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return publicCompilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{id}")
    public CompilationDto getCompilationById(@PathVariable Long id) {
        return publicCompilationService.getCompilationById(id);
    }
}