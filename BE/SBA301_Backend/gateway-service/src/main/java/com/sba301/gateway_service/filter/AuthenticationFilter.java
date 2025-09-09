package com.sba301.gateway_service.filter;

import com.sba301.gateway_service.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba301.gateway_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @Value("${app.api-prefix}")
    private String API_PREFIX;

    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;
    private final PublicUrlMatcher publicUrlMatcher;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath().replaceAll(API_PREFIX, ""); //remove prefix
        //check public
        if (publicUrlMatcher.matches(path))
            return chain.filter(exchange);

        // Get token from the authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse(), "Missing authorization header");

        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Token: {}", token);

        return authenticationService.introspect(token).flatMap(introspectResponse -> {
            log.info("Introspection response: {}", introspectResponse);
            String email = introspectResponse.getResult().getEmail();
            String id = introspectResponse.getResult().getId();

            //set user id and email in request headers
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", id)
                    .header("X-User-Email", email)
                    .build();

            //create new exchange with headers
            ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();

            if (introspectResponse.getResult().isValid())
                return chain.filter(mutatedExchange);
            else
                return unauthenticated(exchange.getResponse(), "Invalid token");
        }).onErrorResume(throwable -> {
            log.info("Introspection failed: {}", throwable.getMessage());
            return unauthenticated(exchange.getResponse());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        return unauthenticated(response, "Unauthenticated");
    }

    Mono<Void> unauthenticated(ServerHttpResponse response, String message) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message(message)
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
