package com.diccionario.lenguamaterna.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AuthUtil {
    private AuthUtil() {}
    public static Long optionalUserId(Authentication authentication) {
        return authentication instanceof JwtAuthenticationToken jwt ? Long.valueOf(jwt.getToken().getSubject()) : null;
    }
    public static Long requireUserId(Authentication authentication) {
        Long id = optionalUserId(authentication);
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión.");
        return id;
    }
}
