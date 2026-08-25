package com.diccionario.lenguamaterna.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SessionUtil {
    private SessionUtil() {}

    public static Long requireUserId(HttpSession session) {
        Object value = session.getAttribute("usuarioId");
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión.");
        }
        return (Long) value;
    }

    public static Long optionalUserId(HttpSession session) {
        Object value = session.getAttribute("usuarioId");
        return value instanceof Long id ? id : null;
    }

    public static Long requireAdmin(HttpSession session) {
        Long id = requireUserId(session);
        Object role = session.getAttribute("rol");
        if (role == null || !"ADMINISTRADOR".equalsIgnoreCase(role.toString())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta acción requiere rol de administrador.");
        }
        return id;
    }
}
