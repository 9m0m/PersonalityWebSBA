package com.sba301.premium_service;

import com.sba301.premium_service.dto.PremiumCreateRequest;
import com.sba301.premium_service.dto.SubcriptionRequest;
import com.sba301.premium_service.repository.PremiumRepository;
import com.sba301.premium_service.repository.SubcriptionRepository;
import com.sba301.premium_service.service.PremiumService;
import com.sba301.premium_service.service.SubcriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class PremiumApplication implements CommandLineRunner {

    private final PremiumRepository premiumRepository;
    private final SubcriptionRepository subcriptionRepository;
    private final PremiumService premiumService;
    private final SubcriptionService subcriptionService;

    public static void main(String[] args) {
        SpringApplication.run(PremiumApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (premiumRepository.count() == 0 && subcriptionRepository.count() == 0) {
            setMockAuthentication();

            premiumService.createPremium(
                    PremiumCreateRequest.builder()
                            .name("AAA")
                            .description("This is a mock premium for testing purposes.")
                            .price(1000)
                            .duration(30)
                            .build()
            );

            premiumService.createPremium(
                    PremiumCreateRequest.builder()
                            .name("BBB")
                            .description("This is another mock premium for testing purposes.")
                            .price(2000)
                            .duration(60)
                            .build()
            );

            premiumService.createPremium(
                    PremiumCreateRequest.builder()
                            .name("CCC")
                            .description("This is yet another mock premium for testing purposes.")
                            .price(3000)
                            .duration(90)
                            .build()
            );

            subcriptionService.createSubscription(
                    SubcriptionRequest.builder()
                            .uid("000-000")
                            .premiumId(1)
                            .build()
            );

            subcriptionService.createSubscription(
                    SubcriptionRequest.builder()
                            .uid("000-001")
                            .premiumId(2)
                            .build()
            );

            subcriptionService.createSubscription(
                    SubcriptionRequest.builder()
                            .uid("000-002")
                            .premiumId(3)
                            .build()
            );
        }
    }

    private void setMockAuthentication() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "HS512")
                .claim("sub", "999")
                .claim("email", "vuhse182692@fpt.edu.vn")
                .build();

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }
}
