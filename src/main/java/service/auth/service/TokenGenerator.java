package service.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private static final int BYTES = 32;

    private final SecureRandom secureRandom;

    public String generarTokenCrudo(){
        byte[] bytes = new byte[BYTES];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashear(String tokenCrudo) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(tokenCrudo.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public Boolean validarHash(String tokenCrudo, String tokenHash){

        String hash = hashear(tokenCrudo);

        return tokenHash.equals(hash);

    }



}
