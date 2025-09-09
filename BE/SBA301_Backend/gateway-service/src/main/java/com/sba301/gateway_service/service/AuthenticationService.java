package com.sba301.gateway_service.service;

import com.sba301.gateway_service.dto.ApiResponse;
import com.sba301.gateway_service.dto.request.IntrospectRequest;
import com.sba301.gateway_service.dto.response.IntrospectResponse;
import com.sba301.gateway_service.repository.AuthenticationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AuthenticationClient authenticationClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return authenticationClient.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
