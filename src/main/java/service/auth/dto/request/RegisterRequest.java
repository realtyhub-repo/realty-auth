package service.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
        )
        String password,

        @NotBlank
        String confirmPassword,

        @NotBlank
        String nombre
) {}