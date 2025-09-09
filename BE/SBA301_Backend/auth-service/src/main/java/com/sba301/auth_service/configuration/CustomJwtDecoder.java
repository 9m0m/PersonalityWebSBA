package com.sba301.auth_service.configuration;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.sba301.auth_service.repository.InvalidatedTokenRepository;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public CustomJwtDecoder(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify signature
            JWSVerifier verifier = new MACVerifier(signerKey);
            if (!signedJWT.verify(verifier)) {
                throw new JwtException("Invalid JWT signature");
            }

            // Check expiration
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw new JwtException("JWT token is expired");
            }

            // Check if token was invalidated (logout, refresh)
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            if (invalidatedTokenRepository.existsById(jti)) {
                throw new JwtException("JWT token has been invalidated");
            }

            return new Jwt(
                    token,
                    toInstant(signedJWT.getJWTClaimsSet().getIssueTime()),
                    toInstant(expiration),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());

        } catch (ParseException e) {
            throw new JwtException("Invalid JWT token format", e);
        } catch (Exception e) {
            throw new JwtException("Failed to decode JWT token", e);
        }
    }

    private Instant toInstant(Date date) {
        return date != null ? date.toInstant() : null;
    }
}
