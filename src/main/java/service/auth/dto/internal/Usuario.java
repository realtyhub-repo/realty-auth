package service.auth.dto.internal;

import service.auth.entity.RolUsuario;

import java.util.UUID;

public record Usuario(
        UUID id,
        RolUsuario rol
) {}