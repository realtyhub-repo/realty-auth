package service.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.auth.dto.request.ForgotPasswordRequest;
import service.auth.dto.request.ResetPasswordRequest;
import service.auth.service.PasswordResetService;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordResetService passwordResetService;


    @PostMapping("/forgot-password")
    public ResponseEntity<Void> solicitarReset(@Valid @RequestBody ForgotPasswordRequest request){

        passwordResetService.solicitarReset(request.email());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> confirmarReset(@Valid @RequestBody ResetPasswordRequest request){

        passwordResetService.confirmarReset(request.token(), request.password());
        return ResponseEntity.ok().build();
    }

}
