package br.com.webflux.model.request;

public record UserRequest(
        String nome,
        String email,
        String password
) {
}