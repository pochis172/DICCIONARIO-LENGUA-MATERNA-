package com.diccionario.lenguamaterna.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SessionUtil {

    private SessionUtil() {
    }

    // =====================================================
    // USUARIO AUTENTICADO
    // =====================================================
    public static Long requireUserId(HttpSession session) {

        Object value = session.getAttribute("usuarioId");

        if (value == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión."
            );
        }

        return (Long) value;
    }


    // =====================================================
    // USUARIO OPCIONAL
    // =====================================================
    public static Long optionalUserId(HttpSession session) {

        Object value = session.getAttribute("usuarioId");

        return value instanceof Long id ? id : null;
    }


    // =====================================================
    // OBTENER EL ROL DE LA SESIÓN
    // =====================================================
    private static String requireRole(HttpSession session) {

        requireUserId(session);

        Object value = session.getAttribute("rol");

        if (!(value instanceof String rol)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "La sesión no contiene un rol válido."
            );
        }

        return rol;
    }


    // =====================================================
    // USUARIO
    // CUALQUIER USUARIO AUTENTICADO
    // =====================================================
    public static Long requireUser(HttpSession session) {

        return requireUserId(session);
    }


    // =====================================================
    // EDITOR
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    public static Long requireEditor(HttpSession session) {

        Long usuarioId = requireUserId(session);

        String rol = requireRole(session);

        if (!rol.equalsIgnoreCase("EDITOR")
                && !rol.equalsIgnoreCase("ADMINISTRADOR")
                && !rol.equalsIgnoreCase("SUPER_USUARIO")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para modificar contenido."
            );
        }

        return usuarioId;
    }


    // =====================================================
    // ADMINISTRADOR
    // ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    public static Long requireAdmin(HttpSession session) {

        Long usuarioId = requireUserId(session);

        String rol = requireRole(session);

        if (!rol.equalsIgnoreCase("ADMINISTRADOR")
                && !rol.equalsIgnoreCase("SUPER_USUARIO")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos de administrador."
            );
        }

        return usuarioId;
    }


    // =====================================================
    // SUPER USUARIO
    // SOLO SUPER_USUARIO
    // =====================================================
    public static Long requireSuperUser(HttpSession session) {

        Long usuarioId = requireUserId(session);

        String rol = requireRole(session);

        if (!rol.equalsIgnoreCase("SUPER_USUARIO")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el SUPER_USUARIO puede realizar esta acción."
            );
        }

        return usuarioId;
    }
}