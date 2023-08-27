package br.com.webflux.controller;

import br.com.webflux.entity.User;
import br.com.webflux.mapper.UserMapper;
import br.com.webflux.model.request.UserRequest;
import br.com.webflux.model.response.UserResponse;
import br.com.webflux.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String ID = "123456";

    public static final String NAME = "test";

    public static final String EMAIL = "test@test.com";

    public static final String PASSWORD = "123";
    public static final String BASE_URI = "/users";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {

        UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);

        Mockito.when(service.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient
                .post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(service, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with bad request")
    void testSaveWithBadRequest() {

        UserRequest request = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);

        webTestClient
                .post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at end");

    }

    @Test
    @DisplayName("Test find by id endpoint with success")
    void testFindByIdWithSuccess() {

        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        Mockito.when(service.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        Mockito.when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient
                .get()
                .uri(BASE_URI + "/123456")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("Test find all endpoint with success")
    void testFindAll() {

        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        Mockito.when(service.findAll()).thenReturn(Flux.just(User.builder().build()));
        Mockito.when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient
                .get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);

    }

    @Test
    @DisplayName("Test update endpoint with success")
    void testUpdateWithSuccess() {

        final UserRequest request = new UserRequest(NAME, EMAIL, PASSWORD);
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        Mockito.when(service.update(anyString(), any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));
        Mockito.when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient
                .patch()
                .uri(BASE_URI + "/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        Mockito.verify(service, times(1)).update(anyString(), any(UserRequest.class));
    }

    @Test
    @DisplayName("Test delete endpoint with success")
    void testDeleteWhitSuccess() {

        Mockito.when(service.delete(anyString())).thenReturn(Mono.just(User.builder().build()));

        webTestClient
                .delete()
                .uri(BASE_URI + "/" + ID)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(service, times(1)).delete(anyString());
    }
}