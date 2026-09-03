package service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import service.auth.dto.internal.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex){
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex){
        return construirRespuesta(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiradoException.class)
    public ResponseEntity<ErrorResponse> handleRefreshExpirado(RefreshTokenExpiradoException ex){
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenRevocadoException.class)
    public ResponseEntity<ErrorResponse> handleRefreshRevocado(RefreshTokenRevocadoException ex){
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNoEncontrado(RefreshTokenNoEncontradoException ex){
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleEmailYaRegistrado(EmailYaRegistradoException ex){
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailNoVerificadoException.class)
    public ResponseEntity<ErrorResponse> handleEmailNoVerificado(EmailNoVerificadoException ex){
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(TokenVerificacionNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTokenVerificacionNoEncontrado(TokenVerificacionNoEncontradoException ex){
        return construirRespuesta(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(TokenVerificacionExpiradoException.class)
    public ResponseEntity<ErrorResponse> handleTokenVerificacionExpiradoException(TokenVerificacionExpiradoException ex){
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoEncontradoException(UsuarioNoEncontradoException ex){
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TokenVerificacionYaUsadoException.class)
    public ResponseEntity<ErrorResponse> handleTokenVerificacionYaUsadoException(TokenVerificacionYaUsadoException ex){
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErros(MethodArgumentNotValidException ex){
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return construirRespuesta(HttpStatus.BAD_REQUEST,mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String mensaje){
        ErrorResponse error = new ErrorResponse(mensaje, status.value(), LocalDateTime.now());
        return ResponseEntity.status(status).body(error);

    }
}
