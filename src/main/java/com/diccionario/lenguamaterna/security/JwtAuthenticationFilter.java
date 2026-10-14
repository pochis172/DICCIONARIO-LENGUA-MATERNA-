package com.diccionario.lenguamaterna.security;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    // FILTRO JWT
    // Se ejecuta una vez por cada petición HTTP
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. LEER HEADER AUTHORIZATION
        // Ejemplo:
        // Authorization: Bearer eyJhbGciOiJIUzI1Ni...
        String authorizationHeader =
                request.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        // 2. SI NO HAY TOKEN
        // Dejamos continuar la petición.
        // SecurityConfig decidirá si la ruta es pública
        // o necesita autenticación.
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // 3. EXTRAER EL TOKEN
        // Quitamos la palabra "Bearer "
        String token =
                authorizationHeader.substring(7);

        try {

            // 4. VALIDAR TOKEN
            jwtService.validarToken(token);

            // 5. EXTRAER DATOS DEL TOKEN
            String correo =
                    jwtService.extraerCorreo(token);

            String rol =
                    jwtService.extraerRol(token);

            // 6. CONVERTIR EL ROL EN AUTORIDAD DE SPRING
            // EDITOR se convierte en ROLE_EDITOR
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + rol
                    );

            // 7. CREAR AUTENTICACIÓN
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            correo,
                            null,
                            List.of(authority)
                    );

            // 8. GUARDAR USUARIO AUTENTICADO
            // EN SPRING SECURITY
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            // 9. CONTINUAR HACIA EL CONTROLLER
            filterChain.doFilter(
                    request,
                    response
            );

        } catch (JwtException | IllegalArgumentException ex) {

            // TOKEN VENCIDO, MODIFICADO O INVÁLIDO
            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpStatus.UNAUTHORIZED.value()
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "error": "Token JWT inválido o expirado."
                    }
                    """
            );
        }
    }
}