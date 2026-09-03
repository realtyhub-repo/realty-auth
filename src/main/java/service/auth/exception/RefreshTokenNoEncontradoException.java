package service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenNoEncontradoException extends RuntimeException {
    public RefreshTokenNoEncontradoException(String message) {
        super(message);
    }
}
