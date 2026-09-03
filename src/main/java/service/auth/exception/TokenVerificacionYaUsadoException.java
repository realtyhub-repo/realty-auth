package service.auth.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenVerificacionYaUsadoException extends RuntimeException {
    public TokenVerificacionYaUsadoException(String message) {
        super(message);
    }
}
