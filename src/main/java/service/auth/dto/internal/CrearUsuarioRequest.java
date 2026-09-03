package service.auth.dto.internal;

public record CrearUsuarioRequest(
        String email,
        String nombre,
        String urlFoto
) {}