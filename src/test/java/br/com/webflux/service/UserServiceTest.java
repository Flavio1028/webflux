package br.com.webflux.service;

import br.com.webflux.entity.User;
import br.com.webflux.mapper.UserMapper;
import br.com.webflux.model.request.UserRequest;
import br.com.webflux.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void testSave() {

        UserRequest request = new UserRequest("test", "teste@teste.com", "123");
        User entity = User.builder().build();

        Mockito.when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        Mockito.when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = service.save(request);

        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testFindById() {

        Mockito.when(repository.findById(anyString())).thenReturn(Mono.just(User.builder()
                .id("123")
                .build()));

        Mono<User> result = service.findById("123");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class
                        && user.getId().equals("123"))
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).findById(anyString());
    }

    @Test
    void testFindAll() {

        Mockito.when(repository.findAll()).thenReturn(Flux.just(User.builder()
                .build()));

        Flux<User> result = service.findAll();

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).findAll();
    }

    @Test
    void testUpdate() {
        UserRequest request = new UserRequest("test", "teste@teste.com", "123");
        User entity = User.builder().id("12345").build();

        Mockito.when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
        Mockito.when(repository.findById(anyString())).thenReturn(Mono.just(entity));
        Mockito.when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        Mono<User> result = service.update("12345", request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals("12345"))
                .expectComplete()
                .verify();

        Mockito.verify(repository, times(1)).save(any(User.class));
    }


}