package service.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import service.auth.dto.internal.CrearUsuarioRequest;
import service.auth.dto.response.UsuarioResponse;
import service.auth.exception.EmailYaRegistradoException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;


    public UsuarioResponse crearUsuario(CrearUsuarioRequest request){

        try {
            return restClient.post()
                    .uri("/usuario/internal")
                    .body(request)
                    .retrieve()
                    .body(UsuarioResponse.class);
        }catch (HttpClientErrorException e){

            throw new EmailYaRegistradoException("Error al crear usuario");

        }

    }

    public UsuarioResponse buscarUsuarioId(UUID usuarioId){
        return restClient.get()
                .uri("/usuario/internal/{id}",usuarioId)
                .retrieve()
                .body(UsuarioResponse.class);
    }


}
