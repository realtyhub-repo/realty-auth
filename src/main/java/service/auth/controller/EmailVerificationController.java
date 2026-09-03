package service.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.auth.dto.request.ResendVerificationRequest;
import service.auth.service.EmailVerificationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verificarEmail(@RequestParam(name = "token") String token){

        verificationService.verificarToken(token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> reenviarEmailVerificacion(@Valid @RequestBody ResendVerificationRequest request){

        verificationService.reenviarVerificacion(request.email());
        return ResponseEntity.ok().build();
    }

}
