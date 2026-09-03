package service.auth.dto.response;

import service.auth.entity.RolUsuario;

import java.util.UUID;

public record UsuarioResponse(UUID id, RolUsuario rolUsuario){
}
