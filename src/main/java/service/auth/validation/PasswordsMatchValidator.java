package service.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import service.auth.dto.request.RegisterRequest;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.password() == null || request.confirmPassword() == null) {
            return true; // deja que @NotBlank se encargue de ese caso por separado
        }
        return request.password().equals(request.confirmPassword());
    }
}