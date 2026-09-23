package service.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

class TokenGeneratorTest {

    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new TokenGenerator(new SecureRandom());
    }

    @Test
    void generarTokenCrudo_noDeberiaSerNuloOVacio() {
        String token = tokenGenerator.generarTokenCrudo();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generarTokenCrudo_deberiaGenerarValoresDistintosCadaVez() {
        String token1 = tokenGenerator.generarTokenCrudo();
        String token2 = tokenGenerator.generarTokenCrudo();
        assertNotEquals(token1, token2);
    }

    @Test
    void validarHash_deberiaSerVerdaderoSiElTokenCoincide() {
        String tokenCrudo = "mi-refresh-token-de-prueba";
        String hash = tokenGenerator.hashear(tokenCrudo);

        assertTrue(tokenGenerator.validarHash(tokenCrudo, hash));
    }

    @Test
    void validarHash_deberiaSerFalsoSiElTokenNoCoincide() {
        String hash = tokenGenerator.hashear("token-correcto");

        assertFalse(tokenGenerator.validarHash("token-incorrecto", hash));
    }
}

