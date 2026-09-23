package com.diccionario.lenguamaterna.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SessionUtil {

    private SessionUtil() {}

    // =====================================================
    // USUARIO OBLIGATORIO
    // Se mantiene para funcionalidades que realmente
    // necesitan saber qué usuario está usando el sistema.
    // Ejemplo: perfil, favoritos, historial y sugerencias.
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
    // Si existe una sesión devuelve el ID.
    // Si no existe, devuelve null.
    // =====================================================
    public static Long optionalUserId(HttpSession session) {

        Object value = session.getAttribute("usuarioId");

        return value instanceof Long id ? id : null;
    }

    // =====================================================
    // VALIDACIÓN DE ADMINISTRADOR
    // TEMPORALMENTE DESHABILITADA
    //
    // En esta etapa del proyecto el profesor indicó que
    // todavía no se debe exigir seguridad para probar
    // los módulos administrativos desde Postman.
    //
    // Por eso Lenguas, Palabras y Traducciones podrán
    // probarse sin iniciar sesión.
    //
    // Cuando corresponda implementar seguridad nuevamente,
    // aquí se restaurará la validación del rol ADMINISTRADOR.
    // =====================================================
    public static Long requireAdmin(HttpSession session) {

        return optionalUserId(session);
    }
}