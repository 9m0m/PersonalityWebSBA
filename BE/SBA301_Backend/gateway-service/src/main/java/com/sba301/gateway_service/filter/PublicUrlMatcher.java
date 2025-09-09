package com.sba301.gateway_service.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;

@Component
public class PublicUrlMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
            //TEST
            "/university/api/universities/test", //universitytest
            "/quiz/api/quiz/test", //quiztest
//            "/authenticate/auth/test", //auth
            "/order",
            "/quiz/categories",
            "/authenticate/users",
            "/authenticate/auth/token",
            "/authenticate/auth/introspect",
            "/authenticate/auth/logout",
            "/authenticate/auth/refresh",
            "/persona/profiles",
            "/authenticate/auth/outbound/authentication",
            "/authenticate/users/verify-otp",
            "/authenticate/users/resend",
            "/authenticate/users/forgot-password/reset",
            "/authenticate/users/forgot-password/verify",

            //premiums
            "/premium/premiums", //get all

            "/event/events" //get all events
    );
    private static final List<String> PUBLIC_WILDCARD_PATTERNS = List.of(

            //premiums
            "/premium/premiums/*", //get by id

            "/event/events/**"
    );

    public boolean matches(String path) {
        return PUBLIC_EXACT_PATHS.contains(path) || PUBLIC_WILDCARD_PATTERNS.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

}
