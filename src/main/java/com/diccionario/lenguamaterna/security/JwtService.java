package com.diccionario.lenguamaterna.security;

import com.diccionario.lenguamaterna.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // CLAVE SECRETA
    // Se obtiene desde application.properties
    // y finalmente desde la variable JWT_SECRET
    @Value("${security.jwt.secret}")
    private String secret;

    // TIEMPO DE EXPIRACIÓN DEL TOKEN
    // Por defecto: 1 hora
    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    // GENERAR TOKEN JWT
    // Recibe el usuario autenticado
    public String generarToken(Usuario usuario) {

        Date ahora = new Date();

        Date expiracion =
                new Date(
                        ahora.getTime() + expirationMs
                );

        return Jwts.builder()

                // Correo del usuario propietario del token
                .subject(usuario.getCorreo())

                // ID del usuario
                .claim(
                        "usuarioId",
                        usuario.getId()
                )

                // Rol del usuario
                .claim(
                        "rol",
                        usuario.getRol().getNombre()
                )

                // Momento en que se creó
                .issuedAt(ahora)

                // Momento en que deja de ser válido
                .expiration(expiracion)

                // Firma digital del token
                .signWith(obtenerClave())

                // Construye el JWT final
                .compact();
    }

    // EXTRAER CORREO DEL TOKEN
    public String extraerCorreo(
            String token
    ) {

        return obtenerClaims(token)
                .getSubject();
    }

    // EXTRAER ID DEL USUARIO
    public Long extraerUsuarioId(
            String token
    ) {

        Number usuarioId =
                obtenerClaims(token)
                        .get(
                                "usuarioId",
                                Number.class
                        );

        return usuarioId.longValue();
    }

    // EXTRAER ROL DEL TOKEN
    public String extraerRol(
            String token
    ) {

        return obtenerClaims(token)
                .get(
                        "rol",
                        String.class
                );
    }

    // VALIDAR TOKEN
    // Si la firma es incorrecta, está vencido
    // o el token está dañado, JJWT lanza una excepción.
    public boolean validarToken(
            String token
    ) {

        obtenerClaims(token);

        return true;
    }

    // LEER TODA LA INFORMACIÓN DEL TOKEN
    private Claims obtenerClaims(
            String token
    ) {

        return Jwts.parser()

                // Verifica la firma
                .verifyWith(obtenerClave())

                .build()

                // Lee y valida el token firmado
                .parseSignedClaims(token)

                // Obtiene sus datos
                .getPayload();
    }

    // CONVERTIR JWT_SECRET EN UNA CLAVE CRIPTOGRÁFICA
    private SecretKey obtenerClave() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }
}