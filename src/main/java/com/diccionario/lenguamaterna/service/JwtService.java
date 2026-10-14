package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

@Service
public class JwtService {
    public record AuthResponse(Long id, String nombre, String correo, String rol,
                               String token, String tokenType, long expiresIn) {}
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long ttl;
    private final ConcurrentHashMap<String, Instant> revoked = new ConcurrentHashMap<>();

    public JwtService(@Value("${security.jwt.secret:}") String secret,
                      @Value("${security.jwt.ttl-seconds:3600}") long ttl,
                      UsuarioRepository users) {
        byte[] bytes;
        if (secret.isBlank()) { bytes = new byte[32]; new SecureRandom().nextBytes(bytes); }
        else bytes = Base64.getDecoder().decode(secret);
        if (bytes.length < 32 || ttl <= 0) throw new IllegalArgumentException("JWT requiere clave de 32 bytes o más y duración positiva.");
        this.ttl = ttl;
        var key = new SecretKeySpec(bytes, "HmacSHA256");
        encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        var verifier = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        verifier.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            new JwtTimestampValidator(java.time.Duration.ZERO), new JwtIssuerValidator("diccionario"),
            jwt -> {
                revoked.entrySet().removeIf(e -> !e.getValue().isAfter(Instant.now()));
                try {
                    var user = users.findById(Long.valueOf(jwt.getSubject()));
                    if (jwt.getExpiresAt() != null && jwt.getId() != null && !revoked.containsKey(jwt.getId())
                        && user.isPresent() && user.get().getRol().getNombre().equals(jwt.getClaimAsString("rol")))
                        return OAuth2TokenValidatorResult.success();
                } catch (RuntimeException ignored) { }
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Usuario, rol o token inválido.", null));
            }));
        decoder = verifier;
    }
    public JwtDecoder decoder() { return decoder; }
    public AuthResponse issue(UsuarioResponse user) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("diccionario").subject(user.id().toString())
            .issuedAt(now).expiresAt(now.plusSeconds(ttl)).id(UUID.randomUUID().toString())
            .claim("rol", user.rol()).build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new AuthResponse(user.id(), user.nombre(), user.correo(), user.rol(), token, "Bearer", ttl);
    }
    public void revoke(Jwt jwt) { revoked.put(jwt.getId(), jwt.getExpiresAt()); }
}
