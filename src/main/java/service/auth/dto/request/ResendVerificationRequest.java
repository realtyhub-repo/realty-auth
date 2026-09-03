package service.auth.dto.request;

import jakarta.validation.constraints.Email;

public record ResendVerificationRequest (
        @Email
        String email

){
}
