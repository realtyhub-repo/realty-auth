package service.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Asunto {
    VERIFICACION("Correo de verificación de cuenta"),
    RESTABLECER_ACCESO("Restablecimiento de contraseña");

    private final String descripcion;
}
