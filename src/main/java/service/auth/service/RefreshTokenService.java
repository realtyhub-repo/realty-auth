package service.auth.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.auth.dto.response.AuthResponse;
import service.auth.dto.response.UsuarioResponse;
import service.auth.entity.RefreshToken;
import service.auth.exception.RefreshTokenExpiradoException;
import service.auth.exception.RefreshTokenNoEncontradoException;
import service.auth.exception.RefreshTokenRevocadoException;
import service.auth.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceClient userServiceClient;
    private final TokenGenerator tokenGenerator;

    private final JwtService jwtService;

    @Value("${REFRESH_EXPLAINED}")
    private Integer REFRESH_EXPLAINED;

    public String crearRefreshToken(UUID userId){

        String tokenPlano = tokenGenerator.generarTokenCrudo();
        String tokenHash = tokenGenerator.hashear(tokenPlano);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expireAt(LocalDateTime.now().plusDays(REFRESH_EXPLAINED))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenPlano;

    }

    public RefreshToken validarRefreshToken(String tokenCrudo){



        String refreshTokenHash = tokenGenerator.hashear(tokenCrudo);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(refreshTokenHash)
                .orElseThrow(() ->
                        new RefreshTokenNoEncontradoException("Token de sesión no válido"));



        if(refreshToken.getRevoked()){
            revocarTodosDelUsuario(refreshToken.getUserId());
            throw new RefreshTokenRevocadoException("Este token ya no es válido");
        }

        if(refreshToken.getExpireAt().isBefore(LocalDateTime.now())){
            throw new RefreshTokenExpiradoException("El token de sesión expiró, inicia sesión de nuevo");
        }

        return refreshToken;
    }


    public AuthResponse rotarRefreshToken(String tokenCrudo){

        RefreshToken tokenValido = validarRefreshToken(tokenCrudo);
        UsuarioResponse usuarioResponse = userServiceClient.buscarUsuarioId(tokenValido.getUserId());

        tokenValido.setRevoked(true);
        refreshTokenRepository.saveAndFlush(tokenValido);
        String nuevoRefreshToken = crearRefreshToken(tokenValido.getUserId());
        String nuevoAccessToken = jwtService.generarAccessToken(
                tokenValido.getUserId(),
                usuarioResponse.rolUsuario()
        );

        return new AuthResponse(nuevoAccessToken,nuevoRefreshToken);
    }

    public void revocarTodosDelUsuario(UUID userId){
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);

        refreshTokens.forEach(t->t.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokens);

    }

    public void logout(String refreshToken){

        String tokenHash = tokenGenerator.hashear(refreshToken);

        Optional<RefreshToken> refreshTokenOptional= refreshTokenRepository.findByTokenHash(tokenHash);

        refreshTokenOptional.ifPresent(
                t->{
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                }
        );



    }

}
