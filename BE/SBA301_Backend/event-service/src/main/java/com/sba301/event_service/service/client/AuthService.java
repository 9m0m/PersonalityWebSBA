package com.sba301.event_service.service.client;


import com.sba301.event_service.config.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthContext authContext;

    private final WebClient baseClient = WebClient.builder()
            .baseUrl("http://localhost:8801/authenticate/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();

    public String getUserEmail(String id) {
        return baseClient.get()
                .uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authContext.getToken()) // ✅ set here
                .retrieve()
                .bodyToMono(Map.class)
                .block()
                .get("email").toString();
    }
}

