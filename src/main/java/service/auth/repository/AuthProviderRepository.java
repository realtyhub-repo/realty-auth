package service.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.auth.entity.AuthProvider;

import java.util.Optional;
import java.util.UUID;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, UUID> {
        Optional<AuthProvider> findByUserId(UUID userID);
        Optional<AuthProvider> findByGoogleId(String googleID);
        Optional<AuthProvider> findByEmail(String email);
}
