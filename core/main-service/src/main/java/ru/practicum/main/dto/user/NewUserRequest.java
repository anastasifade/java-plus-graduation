package ru.practicum.main.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static ru.practicum.main.constants.DtoConstants.*;

public record NewUserRequest(
        @NotBlank(message = "Username cannot be blank.")
        @Size(min = MIN_USERNAME, max = MAX_USERNAME, message = "Username must be within [2-250] symbols.")
        String name,

        @NotBlank(message = "Поле email не может быть пустым.")
        @Email(message = "Неверный формат email.")
        @Size(min = MIN_EMAIL, max = MAX_EMAIL, message = "Email must be within [6-254] symbols.")
        String email
) {

}
