package ru.practicum.main.controller.adminapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main.service.compilation.CompilationService;

@Loggable
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.createCompilation(dto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compilationId) {
        compilationService.deleteCompilation(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto update(@PathVariable Long compilationId,
                                 @Valid @RequestBody UpdateCompilationRequest request) {
        return compilationService.updateCompilation(compilationId, request);
    }

}
