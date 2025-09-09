package com.sba301.auth_service.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sba301.auth_service.dto.request.ProfileCreationRequest;

@FeignClient(name = "persona-service")
public interface ProfileClient {
    @PostMapping(value = "/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    Object createProfile(@RequestBody ProfileCreationRequest request);
}
