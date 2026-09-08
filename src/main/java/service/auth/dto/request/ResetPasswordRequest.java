package service.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import service.auth.validation.PasswordConfirmable;
import service.auth.validation.PasswordMatch;

@PasswordMatch
public record ResetPasswordRequest(

        @NotBlank
        String token,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
        )
        String password,

        @NotBlank
        String confirmPassword


) implements PasswordConfirmable {
}
