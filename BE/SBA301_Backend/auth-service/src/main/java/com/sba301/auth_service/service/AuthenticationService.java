package com.sba301.auth_service.service;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.sba301.auth_service.dto.request.AuthenticationRequest;
import com.sba301.auth_service.dto.request.IntrospectRequest;
import com.sba301.auth_service.dto.request.LogoutRequest;
import com.sba301.auth_service.dto.request.RefreshRequest;
import com.sba301.auth_service.dto.response.AuthenticationResponse;
import com.sba301.auth_service.dto.response.IntrospectResponse;

public interface AuthenticationService {
    AuthenticationResponse outboundAuthenticate(String code);

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
