package service.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.auth.dto.request.GoogleLoginRequest;
import service.auth.dto.request.LoginRequest;
import service.auth.dto.request.RegisterRequest;
import service.auth.dto.response.AccessToken;
import service.auth.dto.response.AuthResponse;
import service.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AccessToken> loginGoogle(@Valid @RequestBody GoogleLoginRequest googleLogin, HttpServletResponse response){

        AuthResponse authResponse = authService.loginConGoogle(googleLogin.id_token());

        Cookie cookie =authService.crearCookie("refresh_token", authResponse.refresh_token());
        response.addCookie(cookie);

        return ResponseEntity.ok().body(new AccessToken(authResponse.access_token()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessToken> loginLocal(@Valid @RequestBody LoginRequest request, HttpServletResponse response){

        AuthResponse authResponse = authService.loginLocal(request);

        Cookie cookie =authService.crearCookie("refresh_token", authResponse.refresh_token());
        response.addCookie(cookie);

        return ResponseEntity.ok().body(new AccessToken(authResponse.access_token()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegisterRequest request){
        authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
