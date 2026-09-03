package service.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(

        @NotBlank
        String id_token

){
}
