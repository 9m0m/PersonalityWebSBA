package com.sba301.auth_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sba301.auth_service.dto.request.*;
import com.sba301.auth_service.dto.response.AuthenticationResponse;
import com.sba301.auth_service.dto.response.IntrospectResponse;
import com.sba301.auth_service.entity.InvalidatedToken;
import com.sba301.auth_service.entity.Users;
import com.sba301.auth_service.exception.AppException;
import com.sba301.auth_service.exception.ErrorCode;
import com.sba301.auth_service.repository.InvalidatedTokenRepository;
import com.sba301.auth_service.repository.UserRepository;
import com.sba301.auth_service.repository.httpclient.OutboundAuthenticationClient;
import com.sba301.auth_service.repository.httpclient.OutboundUserClient;
import com.sba301.event.CreatedUserEvent;
import com.sba301.event.NotificationEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundAuthenticationClient outboundAuthenticationClient;
    OutboundUserClient outboundUserClient;
    KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    EventPublisher eventPublisher;

    public AuthenticationResponse outboundAuthenticate(String code) {
        var response = outboundAuthenticationClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        if (userRepository.findByEmail(userInfo.getEmail()).isEmpty()) {
            var newUser = userRepository.save(Users.builder()
                    .email(userInfo.getEmail())
                    .emailVerified(userInfo.isVerifiedEmail())
                    .role(Users.Role.STUDENT)
                    .build());

            try {
                eventPublisher.sendUserCreated(
                        new CreatedUserEvent(newUser.getId(), null, null, null, null, null, null));

                eventPublisher.sendNotification(NotificationEvent.builder()
                        .channel("EMAIL")
                        .recipient(newUser.getEmail())
                        .subject("Welcome!")
                        .templateCode("welcome_email")
                        .param(Map.of("user", newUser.getEmail()))
                        .build());
            } catch (Exception e) {
                log.error("Error while creating user profile: {}", e.getMessage());
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            var token = generateToken(newUser);
            return AuthenticationResponse.builder().token(token.token).build();
        } else {
            var existingUser = userRepository
                    .findByEmail(userInfo.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            if (!existingUser.isEmailVerified()) {
                existingUser.setEmailVerified(userInfo.isVerifiedEmail());
                userRepository.save(existingUser);
            }
            var token = generateToken(existingUser);
            return AuthenticationResponse.builder()
                    .token(token.token)
                    .expiryTime(token.expiryDate)
                    .build();
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {

        var token = request.getToken();
        boolean isValid = true;
        String userId = null;
        String email = null;

        try {
            var signedJWT = verifyToken(token);
            userId = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
            email = (String) signedJWT.getJWTClaimsSet().getClaim("email");
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .id(userId)
                .email(email)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (!user.isEmailVerified()) {
            throw new AppException(ErrorCode.UNVERIFIED_EMAIL);
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token.token)
                .expiryTime(token.expiryDate)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token.token)
                .expiryTime(token.expiryDate)
                .build();
    }

    private TokenInfo generateToken(Users user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expiryTime = new Date(Instant.ofEpochMilli(issueTime.getTime())
                .plus(1, ChronoUnit.HOURS)
                .toEpochMilli());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issueTime(issueTime)
                .expirationTime(expiryTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return new TokenInfo(jwsObject.serialize(), expiryTime);
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(Users user) {
        if (user.getRole() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return "ROLE_" + user.getRole().name();
    }

    private record TokenInfo(String token, Date expiryDate) {}
}
