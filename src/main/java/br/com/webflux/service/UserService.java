package br.com.webflux.service;

import br.com.webflux.entity.User;
import br.com.webflux.mapper.UserMapper;
import br.com.webflux.model.request.UserRequest;
import br.com.webflux.repository.UserRepository;
import br.com.webflux.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request) {
        return userRepository.save(mapper.toEntity(request));
    }

    public Mono<User> findById(final String id) {
        return handlerNotFound(userRepository.findById(id), id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request) {
        return findById(id).map(entity -> mapper.toEntity(request, entity)).flatMap(userRepository::save);
    }

    public Mono<User> delete(final String id) {
        return handlerNotFound(userRepository.findAndRemove(id), id);
    }

    private <T> Mono<T> handlerNotFound(Mono<T> mono, String id) {
        return mono.switchIfEmpty(Mono.error(new ObjectNotFoundException(format("Object not found. Id: %s, Type: %s ", id, User.class.getSimpleName()))));
    }

}