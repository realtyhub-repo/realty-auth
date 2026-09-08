package service.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.auth.dto.request.GoogleLoginRequest;
import service.auth.dto.request.LoginRequest;
import service.auth.dto.request.RegisterRequest;
import service.auth.dto.response.AccessToken;
import service.auth.dto.response.AuthResponse;
import service.auth.exception.RefreshTokenNoEncontradoException;
import service.auth.service.AuthService;
import service.auth.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/google")
    public ResponseEntity<AccessToken> loginGoogle(@Valid @RequestBody @NonNull GoogleLoginRequest googleLogin){

        AuthResponse authResponse = authService.loginConGoogle(googleLogin.id_token());
        ResponseCookie refreshCookie = authService.crearCookie("refresh_token", authResponse.refresh_token());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessToken(authResponse.access_token()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessToken> loginLocal(@Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.loginLocal(request);

        ResponseCookie refreshCookie = authService.crearCookie("refresh_token", authResponse.refresh_token());


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessToken(authResponse.access_token()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegisterRequest request) {
        authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessToken> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {

        if(refreshToken==null ){
            throw new RefreshTokenNoEncontradoException("Token de sesión proporcionado");
        }

        AuthResponse authResponse = refreshTokenService.rotarRefreshToken(refreshToken);

        ResponseCookie refreshCookie = authService.crearCookie("refresh_token", authResponse.refresh_token());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessToken(authResponse.access_token()));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ){

        if(refreshToken==null ){
            throw new RefreshTokenNoEncontradoException("Token de sesión proporcionado");
        }
        refreshTokenService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token","")
                .httpOnly(true)
                .secure(false) // debo cambiar este valor porque permite llamadas en HTTP
                .path("/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }


}
