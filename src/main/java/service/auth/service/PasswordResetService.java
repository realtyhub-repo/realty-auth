package service.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.auth.entity.*;
import service.auth.exception.TokenVerificacionExpiradoException;
import service.auth.exception.TokenVerificacionNoEncontradoException;
import service.auth.exception.TokenVerificacionYaUsadoException;
import service.auth.exception.UsuarioNoEncontradoException;
import service.auth.repository.AuthProviderRepository;
import service.auth.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    @Value("${PASSWORD_RESET}")
    private long minutosExpiracion;

    @Value("${FRONTEND_URL:http://localhost:8081}")
    private String frontendUrl;

    private final AuthProviderRepository authProviderRepository;
    private final VerificationTokenRepository verificationRepository;
    private final EmailService emailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public void solicitarReset(String email){

        Optional<AuthProvider> porEmail = authProviderRepository.findByEmail(email);

        if(porEmail.isEmpty()) return;

        if(porEmail.get().getProveedor()== Proveedor.GOOGLE) return;

        AuthProvider authProvider = porEmail.get();

        Optional<VerificationToken> tokenOptional = verificationRepository.
                findByUserIdAndTipoAndUsedFalse(
                        authProvider.getUserId(),
                        TipoVerificacion.PASSWORD_RESET
                );

        tokenOptional.ifPresent(
                t-> {
                    t.setUsed(true);
                    verificationRepository.save(t);
                }
        );

        String tokenCrudo = tokenGenerator.generarTokenCrudo();
        String tokenHash  = tokenGenerator.hashear(tokenCrudo);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(minutosExpiracion);


        VerificationToken verificationToken = VerificationToken.builder()
                .userId(authProvider.getUserId())
                .tokenHash(tokenHash)
                .tipo(TipoVerificacion.PASSWORD_RESET)
                .expiresAt(expiresAt)
                .build();

        verificationRepository.save(verificationToken);

        String link = frontendUrl + "/reset-password?token="+tokenCrudo;

        emailService.enviarCorreo(email,link, Asunto.RESTABLECER_ACCESO);

    }

    @Transactional
    public void confirmarReset(String tokenCrudo, String nuevaPasswordPlano){
        String tokenHash = tokenGenerator.hashear(tokenCrudo);

        VerificationToken verificationToken  =  verificationRepository.findByTokenHashAndTipo(tokenHash, TipoVerificacion.PASSWORD_RESET)
                .orElseThrow(()->
                        new TokenVerificacionNoEncontradoException("El enlace de verificación no es válido"));



        if(verificationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenVerificacionExpiradoException("El enlace de verificación expiró, solicita uno nuevo");
        }

        if (verificationToken.getUsed()){
            throw new TokenVerificacionYaUsadoException("Este enlace ya fue utilizado");
        }

        verificationToken.setUsed(true);
        verificationRepository.save(verificationToken);

        AuthProvider authProvider = authProviderRepository.findByUserId(verificationToken.getUserId())
                .orElseThrow(()->
                        new UsuarioNoEncontradoException("Usuario no encontrado")
                );


        String passwordHash = passwordEncoder.encode(nuevaPasswordPlano);

        authProvider.setPasswordHash(passwordHash);
        authProviderRepository.save(authProvider);


        refreshTokenService.revocarTodosDelUsuario(authProvider.getUserId());
    }

}
