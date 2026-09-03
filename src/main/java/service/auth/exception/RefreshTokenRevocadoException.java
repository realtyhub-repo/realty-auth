package service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenRevocadoException extends RuntimeException {
    public RefreshTokenRevocadoException(String message) {
        super(message);
    }
}
