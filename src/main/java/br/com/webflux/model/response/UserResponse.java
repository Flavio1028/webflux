package br.com.webflux.model.response;

public record UserResponse(
        String id,
        String nome,
        String email,
        String password
) {

}
