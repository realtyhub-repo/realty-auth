package service.auth.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordMatch, PasswordConfirmable> {

    @Override
    public boolean isValid(PasswordConfirmable request, ConstraintValidatorContext context) {
        if (request.password() == null || request.confirmPassword() == null) {
            return true;
        }
        return request.password().equals(request.confirmPassword());
    }
}