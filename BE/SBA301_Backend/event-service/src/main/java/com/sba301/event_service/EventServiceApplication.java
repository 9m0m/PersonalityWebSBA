package com.sba301.event_service;

import com.sba301.event_service.dto.EventCreateRequest;
import com.sba301.event_service.dto.ShowTimeCreateRequest;
import com.sba301.event_service.dto.TicketCreateRequest;
import com.sba301.event_service.repository.EventRepository;
import com.sba301.event_service.service.EventService;
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

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@RequiredArgsConstructor
public class EventServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}


	private final EventService eventService;
	private final EventRepository eventRepository;
	@Override
	public void run(String... args) throws Exception {
		if (eventRepository.count() == 0) {
			setMockAuthentication();

			eventService.createDraftEvent(EventCreateRequest.builder()
					.name("Mock Event")
					.slug("mock-event")
					.description("This is a mock event for testing purposes.")
					.bannerUrl("https://salt.tkbcdn.com/ts/ds/ee/9a/a5/376e7d6cb3659fe613ea154ceb667219.jpg")
					.personalityTypes("INTJ,ENTP,ISFJ")
					.showtimes(List.of(
							ShowTimeCreateRequest.builder()
									.startTime(LocalDateTime.now().plusDays(1))
									.endTime(LocalDateTime.now().plusDays(1).plusHours(2))
									.tickets(List.of(
											TicketCreateRequest.builder()
													.name("Standard Ticket")
													.description("Access to the event")
													.price(10000)
													.quantity(100)
													.startTime(LocalDateTime.now().plusDays(1).plusHours(1))
													.endTime(LocalDateTime.now().plusDays(1).plusHours(2))
													.build(),
											TicketCreateRequest.builder()
													.name("VIP Ticket")
													.description("Access to VIP area")
													.price(20000)
													.quantity(50)
													.startTime(LocalDateTime.now().plusDays(1).plusHours(1))
													.endTime(LocalDateTime.now().plusDays(1).plusHours(2))
													.build()
									))
									.build()
					))
					.build()
			);

			eventService.createAndSubmitEvent(EventCreateRequest.builder()
					.name("Mock Event 2")
					.slug("mock-event-2")
					.description("This is another mock event for testing purposes.")
					.bannerUrl("https://salt.tkbcdn.com/ts/ds/71/ee/a6/e8afda43ecb70a803693813577f2dff3.jpg")
					.personalityTypes("ENFP,ISTJ,ESFJ,creative thinking")
					.showtimes(List.of(
							ShowTimeCreateRequest.builder()
									.startTime(LocalDateTime.now().plusMinutes(2))
									.endTime(LocalDateTime.now().plusDays(2).plusHours(3))
									.tickets(List.of(
											TicketCreateRequest.builder()
													.name("Standard Ticket 2")
													.description("Access to the second mock event")
													.price(15000)
													.quantity(200)
													.startTime(LocalDateTime.now().plusMinutes(2))
													.endTime(LocalDateTime.now().plusMinutes(5))
													.build(),
											TicketCreateRequest.builder()
													.name("VIP Ticket 2")
													.description("Access to VIP area of the second mock event")
													.price(25000)
													.quantity(100)
													.startTime(LocalDateTime.now().plusMinutes(2))
													.endTime(LocalDateTime.now().plusMinutes(5))
													.build()
									))
									.build()
					))
					.build()
			);


		}
	}

	private void setMockAuthentication() {
		Jwt jwt = Jwt.withTokenValue("mock-token")
				.header("alg", "HS512")
				.claim("sub", "222")
				.claim("email", "vuhmpr@gmail.com")
				.build();

		JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, List.of(
				new SimpleGrantedAuthority("ROLE_EVENT_ORGANIZER")
		));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authToken);
		SecurityContextHolder.setContext(context);
	}
}
