package com.diccionario.lenguamaterna;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:security;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
    "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop",
    "security.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="
})
class SecurityIntegrationTests {
    @LocalServerPort int port;
    @org.springframework.beans.factory.annotation.Autowired
    com.diccionario.lenguamaterna.repository.UsuarioRepository users;
    @org.springframework.beans.factory.annotation.Autowired
    com.diccionario.lenguamaterna.repository.RolRepository roles;
    private final HttpClient client = HttpClient.newHttpClient();
    private HttpResponse<String> request(String method, String path, String body, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return client.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
    private String login(String email) throws Exception {
        var res = request("POST", "/api/auth/login", "{\"correo\":\"" + email + "\",\"contrasena\":\"123456\"}", null);
        assertEquals(200, res.statusCode(), res.body());
        var matcher = java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(res.body());
        assertTrue(matcher.find(), res.body());
        return matcher.group(1);
    }
    @Test void publicDictionaryAndRegistration() throws Exception {
        assertEquals(200, request("GET", "/api/diccionario/lenguas", null, null).statusCode());
        var res = request("POST", "/api/auth/register", "{\"nombre\":\"Demo JWT\",\"correo\":\"demo@example.com\",\"contrasena\":\"Demo12345*\",\"aceptaTerminos\":true,\"rol\":\"SUPER_USUARIO\"}", null);
        assertEquals(201, res.statusCode(), res.body());
        assertTrue(res.body().contains("\"rol\":\"USUARIO\""));
        assertTrue(res.body().contains("\"token\":"));
        assertFalse(res.body().contains("contrasena"));
    }
    @Test void validTokenAndNoSessionCookie() throws Exception {
        var res = request("GET", "/api/auth/me", null, login("karen_usuario@diccionario.local"));
        assertEquals(200, res.statusCode(), res.body());
        assertTrue(res.headers().allValues("Set-Cookie").isEmpty());
    }
    @Test void absentAndInvalidTokensAre401() throws Exception {
        assertEquals(401, request("GET", "/api/auth/me", null, null).statusCode());
        assertEquals(401, request("GET", "/api/auth/me", null, "invalid.jwt.value").statusCode());
        assertEquals(401, request("POST", "/api/auth/login", "{\"correo\":\"karen_usuario@diccionario.local\",\"contrasena\":\"incorrecta\"}", null).statusCode());
    }
    @Test void expiredAndTamperedTokensAre401() throws Exception {
        var key = new SecretKeySpec("01234567890123456789012345678901".getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        var claims = JwtClaimsSet.builder().issuer("diccionario").subject("1").id("expired")
            .issuedAt(Instant.now().minusSeconds(120)).expiresAt(Instant.now().minusSeconds(60)).claim("rol", "SUPER_USUARIO").build();
        var expired = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        assertEquals(401, request("GET", "/api/auth/me", null, expired).statusCode());
        String valid = login("karen_usuario@diccionario.local");
        String tampered = valid.substring(0, valid.lastIndexOf('.') + 1) + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        assertEquals(401, request("GET", "/api/auth/me", null, tampered).statusCode());
    }
    @Test void roleRestrictionsAre403() throws Exception {
        String user = login("karen_usuario@diccionario.local");
        assertEquals(403, request("GET", "/api/admin/palabras", null, user).statusCode());
        assertEquals(403, request("GET", "/api/usuarios", null, user).statusCode());
        String editor = login("vanessa_editor@diccionario.local");
        assertEquals(200, request("GET", "/api/admin/palabras", null, editor).statusCode());
        assertEquals(403, request("DELETE", "/api/admin/palabras/999999", null, editor).statusCode());
        assertEquals(403, request("GET", "/api/admin/sugerencias", null, editor).statusCode());
    }
    @Test void administratorAndSuperUserAccess() throws Exception {
        String admin = login("kevin_administrador@diccionario.local");
        assertEquals(200, request("GET", "/api/admin/palabras", null, admin).statusCode());
        assertEquals(403, request("GET", "/api/usuarios", null, admin).statusCode());
        assertEquals(200, request("GET", "/api/usuarios", null, login("alexis_superusuario@diccionario.local")).statusCode());
    }
    @Test void logoutRevokesToken() throws Exception {
        String token = login("karen_usuario@diccionario.local");
        assertEquals(200, request("POST", "/api/auth/logout", null, token).statusCode());
        assertEquals(401, request("GET", "/api/auth/me", null, token).statusCode());
    }
    @Test void changedRoleInvalidatesOldToken() throws Exception {
        String token = login("vanessa_editor@diccionario.local");
        var user = users.findByCorreoIgnoreCase("vanessa_editor@diccionario.local").orElseThrow();
        var original = user.getRol();
        try {
            user.setRol(roles.findByNombreIgnoreCase("USUARIO").orElseThrow());
            users.saveAndFlush(user);
            assertEquals(401, request("GET", "/api/admin/palabras", null, token).statusCode());
        } finally {
            user.setRol(original);
            users.saveAndFlush(user);
        }
    }
}
