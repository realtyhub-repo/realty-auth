package service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailNoVerificadoException extends RuntimeException {
    public EmailNoVerificadoException(String message) {
        super(message);
    }
}
