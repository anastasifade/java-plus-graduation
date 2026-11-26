package ru.practicum.main.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.logging.Loggable;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.service.category.CategoryService;

import java.util.List;

@Loggable
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
}
