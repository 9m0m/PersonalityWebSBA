package com.sba301.persona_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
@EnableDiscoveryClient
public class PersonaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonaApplication.class, args);
    }
}
