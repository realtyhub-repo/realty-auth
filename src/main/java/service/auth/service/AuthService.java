package service.auth.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.auth.dto.request.LoginRequest;
import service.auth.entity.Proveedor;
import service.auth.dto.internal.CrearUsuarioRequest;
import service.auth.dto.internal.Usuario;
import service.auth.dto.request.RegisterRequest;
import service.auth.dto.response.AuthResponse;
import service.auth.dto.response.UsuarioResponse;
import service.auth.entity.AuthProvider;
import service.auth.exception.CredencialesInvalidasException;
import service.auth.exception.EmailNoVerificadoException;
import service.auth.exception.EmailYaRegistradoException;
import service.auth.repository.AuthProviderRepository;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${COOKIE_EXPLAIN}")
    private long COOKIE_EXPLAIN;

    private final AuthProviderRepository authProviderRepository;
    private final GoogleAuthService googleAuthService;
    private final UserServiceClient userServiceClient;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService verificationService;

    public AuthResponse loginConGoogle(String idToken) {
        Usuario usuario = procesarLoginGoogle(idToken);
        return generarTokensPara(usuario);
    }

    public AuthResponse loginLocal(LoginRequest request) {
        Usuario usuario = procesarLoginLocal(request);


        return generarTokensPara(usuario);
    }

    public ResponseCookie crearCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // debo cambiar este valor porque permite llamadas en HTTP
                .path("/auth")
                .maxAge(Duration.ofDays(COOKIE_EXPLAIN))
                .sameSite("Strict")
                .build();

    }

    /*
     * Cambiar a public una
     * vez este terminado, aún no envía correo de
     * verificación de email
     * */
    public void registrar(RegisterRequest request) {

        Optional<AuthProvider> porEmail = authProviderRepository.findByEmail(request.email());

        if (porEmail.isPresent()) {
            throw new EmailYaRegistradoException("Este correo ya está registrado con " + porEmail.get().getProveedor());
        }

        String passwordHash = passwordEncoder.encode(request.password());

        CrearUsuarioRequest crearUsuarioRequest = new CrearUsuarioRequest(request.email(), request.nombre(), null);

        UsuarioResponse usuarioResponse = userServiceClient.crearUsuario(crearUsuarioRequest);

        AuthProvider authProvider = AuthProvider.builder()
                .userId(usuarioResponse.id())
                .proveedor(Proveedor.LOCAL)
                .email(request.email())
                .googleId(null)
                .passwordHash(passwordHash)
                .emailVerificado(false)
                .build();

        authProviderRepository.save(authProvider);
        verificationService.enviarVerificacion(request.email(), usuarioResponse.id());

    }

    /*
     * Estos métodos son privados
     * porque solo se usaran a nivel de clase y no deberían
     *  ser usadas en instancias de dicha clase
     * */

    private Usuario procesarLoginLocal(LoginRequest request) {

        AuthProvider authProvider = authProviderRepository.findByEmail(request.email())

                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!authProvider.getEmailVerificado()) {
            throw new EmailNoVerificadoException("Debes verificar tu correo antes de iniciar sesión");
        }

        if (authProvider.getProveedor() == Proveedor.GOOGLE) {
            //Esta excepción debe ser personalizada
            throw new EmailYaRegistradoException("Este correo esta registrado con Google");
        }


        if (!passwordEncoder.matches(request.password(), authProvider.getPasswordHash())) {
            //Esta excepción debe ser personalizada
            throw new CredencialesInvalidasException("Este email o contraseña incorrecta");

        }

        return construirUsuarioDesde(authProvider);
    }

    private Usuario procesarLoginGoogle(String idToken) {
        Payload payload = googleAuthService.verificarToken(idToken);
        String googleId = payload.getSubject();
        String email = payload.getEmail();

        Optional<AuthProvider> porGoogleId = authProviderRepository.findByGoogleId(googleId);

        if (porGoogleId.isPresent()) {
            return construirUsuarioDesde(porGoogleId.get());
        }

        Optional<AuthProvider> porEmail = authProviderRepository.findByEmail(email);
        if (porEmail.isPresent()) {
            throw new EmailYaRegistradoException("Este correo ya esta registrado con " + porEmail.get().getProveedor());

        }

        return crearUsuarioNuevoConGoogle(payload);
    }


    private Usuario crearUsuarioNuevoConGoogle(Payload payload) {

        CrearUsuarioRequest request = new CrearUsuarioRequest(
                payload.getEmail(),
                (String) payload.get("name"),
                (String) payload.get("picture")
        );

        UsuarioResponse usuarioResponse = userServiceClient.crearUsuario(request);

        AuthProvider authProvider = AuthProvider.builder()
                .userId(usuarioResponse.id())
                .proveedor(Proveedor.GOOGLE)
                .email(payload.getEmail())
                .googleId(payload.getSubject())
                .passwordHash(null)
                .emailVerificado(true)
                .build();

        authProviderRepository.save(authProvider);
        return new Usuario(usuarioResponse.id(), usuarioResponse.rolUsuario());
    }


    private Usuario construirUsuarioDesde(AuthProvider authProvider) {
        UsuarioResponse usuarioResponse = userServiceClient.buscarUsuarioId(authProvider.getUserId());

        return new Usuario(
                usuarioResponse.id(),
                usuarioResponse.rolUsuario()
        );
    }


    private AuthResponse generarTokensPara(Usuario usuario) {
        String refreshToken = refreshTokenService.crearRefreshToken(usuario.id());
        String accessToken = jwtService.generarAccessToken(usuario.id(), usuario.rol());
        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }


}
