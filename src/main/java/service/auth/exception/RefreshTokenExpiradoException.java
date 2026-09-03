package service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpiradoException extends RuntimeException {
    public RefreshTokenExpiradoException(String message) {
        super(message);
    }
}
