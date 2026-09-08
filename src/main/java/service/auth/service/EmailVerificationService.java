package service.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.auth.entity.Asunto;
import service.auth.entity.AuthProvider;
import service.auth.entity.VerificationToken;
import service.auth.entity.TipoVerificacion;
import service.auth.exception.*;
import service.auth.repository.AuthProviderRepository;
import service.auth.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${EMAIL_TOKEN_EXPLAINED}")
    private Integer EMAIL_TOKEN_EXPLAINED;

    @Value("${FRONTEND_URL:http://localhost:8081}")
    private String frontendUrl;

    private final EmailService emailService;
    private final TokenGenerator tokenGenerator;
    private final VerificationTokenRepository verificationRepository;
    private final AuthProviderRepository authProviderRepository;

    @Transactional
    public void enviarVerificacion(String email, UUID userId){

        String token = tokenGenerator.generarTokenCrudo();
        String tokenHash = tokenGenerator.hashear(token);

        VerificationToken verificationToken = VerificationToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .tipo(TipoVerificacion.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusDays(EMAIL_TOKEN_EXPLAINED))
                .build();


        verificationRepository.save(verificationToken);

        String linkVerificacion = frontendUrl +
                "/verify-email?token=" +
                token;

        emailService.enviarCorreo(email, linkVerificacion, Asunto.VERIFICACION);

    }

    @Transactional
    public void verificarToken(String tokenCrudo){

        String tokenHash = tokenGenerator.hashear(tokenCrudo);

        VerificationToken verificationToken  =  verificationRepository.findByTokenHashAndTipo(tokenHash, TipoVerificacion.EMAIL_VERIFICATION)
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

        authProvider.setEmailVerificado(true);

        authProviderRepository.save(authProvider);
    }

    @Transactional
    public void reenviarVerificacion(String email){
        Optional<AuthProvider> authProviderOp = authProviderRepository.findByEmail(email);

        if(authProviderOp.isEmpty()){
            return;
        }

        AuthProvider authProvider = authProviderOp.get();

        if (authProvider.getEmailVerificado()){return;}

        Optional<VerificationToken> tokenAnterior = verificationRepository.findByUserIdAndTipoAndUsedFalse(
                authProvider.getUserId(),
                TipoVerificacion.EMAIL_VERIFICATION
        );

        tokenAnterior.ifPresent(token->{
            token.setUsed(true);
            verificationRepository.save(token);
                });


        enviarVerificacion(email,authProvider.getUserId());
    }




}
