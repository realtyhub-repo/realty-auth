package service.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.auth.entity.TipoVerificacion;
import service.auth.entity.VerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByTokenHash(String tokenHash);
    Optional<VerificationToken> findByUserIdAndTipoAndUsedFalse(UUID userId, TipoVerificacion tipo);
}
