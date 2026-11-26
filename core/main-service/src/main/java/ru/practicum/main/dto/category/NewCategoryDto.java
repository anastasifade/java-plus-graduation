package ru.practicum.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static ru.practicum.main.constants.DtoConstants.MAX_TITLE;

public record NewCategoryDto(
        @NotBlank(message = "Category name cannot be blank.")
        @Size(max = MAX_TITLE, message = "Category name cannot be over 50 symbols.")
        String name) {
}