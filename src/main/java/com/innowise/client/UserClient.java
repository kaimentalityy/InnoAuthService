package com.innowise.client;

import com.innowise.model.dto.UserRegisterDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class UserClient {

    private final WebClient userServiceClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        this.userServiceClient = webClientBuilder.baseUrl("http://user-service:8082").build();
    }

    public void createUserInUserService(UserRegisterDto request) {
        try {
            userServiceClient.post()
                    .uri("/api/users/register")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("UserService error: " + msg))
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("UserService returned error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in UserService", e);
        }
    }

    public void deleteUserInUserService(String email) {
        try {
            userServiceClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/api/users/internal/{email}").build(email))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("UserService rollback failed: " + e.getResponseBodyAsString(), e);
        }
    }

}
