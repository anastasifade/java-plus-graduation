package ru.practicum.main.controller.adminapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.service.category.CategoryService;

@Loggable
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto dto) {
        return categoryService.createCategory(dto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto update(@PathVariable Long categoryId, @Valid @RequestBody NewCategoryDto dto) {
        return categoryService.updateCategory(categoryId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }
}