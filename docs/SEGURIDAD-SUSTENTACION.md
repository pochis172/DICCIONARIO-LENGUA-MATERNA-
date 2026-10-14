# Sustentación individual: seguridad del backend

## Cumplimiento de la guía
- Dependencias: Spring Security, OAuth2 Resource Server y JOSE/Nimbus (firma y validación JWT); BCrypt para contraseñas.
- Configuración central: `SecurityConfig.java`, filtro Bearer provisto por Spring y política STATELESS.
- Generación/validación: `JwtService.java`; JWT HS256 con `iss`, `sub`, `iat`, `exp`, `jti` y `rol`.
- Protección por método: `@PreAuthorize` en cada función de los controladores. Los roles no se asignan por prefijo de URL.
- Respuestas: registro 201, login y consulta autorizada 200; sin token, alterado, expirado o revocado 401; rol insuficiente 403.
- El rol y la existencia del usuario se comprueban contra la base de datos al validar el token: cambiar el rol o eliminar al usuario invalida sus tokens anteriores.

## Arranque independiente
Requiere Java 17 o superior. Para demostrar sin instalar MySQL:
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```
Abre http://localhost:8080. El perfil local usa H2 persistente en `data/`. El perfil predeterminado conserva MySQL; usa DB_URL, DB_USER y DB_PASSWORD si corresponde.

Importa `docs/postman/Seguridad.postman_collection.json` en Postman. La colección guarda automáticamente los tokens; no necesita cookies ni personas adicionales.

## Cuentas iniciales para la demostración
En una base nueva existen estas cuentas con contraseña `123456`:
| Correo | Rol |
|---|---|
| karen_usuario@diccionario.local | USUARIO |
| vanessa_editor@diccionario.local | EDITOR |
| kevin_administrador@diccionario.local | ADMINISTRADOR |
| alexis_superusuario@diccionario.local | SUPER_USUARIO |

Si ya cambiaste la contraseña, actualiza la variable `demoPassword` en Postman. El inicializador ahora conserva contraseñas y roles existentes.

## Secuencia de exposición (8-10 minutos)
1. Presenta el objetivo del diccionario (30 segundos).
2. Muestra pom.xml y explica BCrypt y JOSE/Nimbus (1 minuto).
3. Recorre AuthController, AuthService y JwtService: validar credenciales, firmar token y devolverlo (2 minutos).
4. Muestra SecurityConfig: Bearer, validación de firma y expiración, sesión STATELESS y roles provenientes del claim `rol` (1 minuto).
5. Muestra un método con @PreAuthorize en AdminController y otro en UsuariosController; explica la matriz (1 minuto).
6. Ejecuta la colección en orden: público, registro, 200 con token, 403 con usuario, 401 sin token/inválido, login de administrador y 200 autorizado (3 minutos).
7. Cierra sesión y demuestra que reutilizar el token produce 401.

Frase clave: «401 significa autenticación ausente o inválida; 403 significa que la autenticación es válida pero el rol no permite esa función».

## Token expirado en vivo
Para una demostración adicional, inicia una instancia con JWT_TTL_SECONDS=10, inicia sesión y guarda su token. Espera más de 10 segundos y consulta `/api/auth/me` con ese mismo token: debe devolver 401. Después restaura la duración normal de 3600 segundos. El caso de expiración también está cubierto por una prueba automatizada, sin esperas.

## Archivos nuevos y modificados
- Nuevos: SecurityConfig, JwtService, AuthUtil, SecurityIntegrationTests, esta matriz y colección Postman.
- Modificados: pom.xml, AuthService y todos los controladores (sesiones reemplazadas por Authentication y anotaciones por método), ApiExceptionHandler, application.properties y clientes JS (Bearer y almacenamiento por pestaña).
- Retirado: SessionUtil, sustituido por AuthUtil para obtener el usuario desde el JWT.
- DataInitializer deja de restablecer contraseñas/roles al reiniciar.

## Configuración y límites
JWT_SECRET admite una clave Base64 de al menos 32 bytes. Si no se configura, se genera una clave aleatoria al arrancar: reiniciar invalida tokens anteriores. JWT_TTL_SECONDS controla la duración.
Logout revoca el jti en memoria hasta su expiración. Con una clave fija, esa lista se pierde al reiniciar; para varias instancias o revocación persistente se requiere almacenamiento compartido. Esta entrega está diseñada para la demostración académica en una instancia.
La web conserva el token en sessionStorage y lo envía como Authorization: Bearer. El backend no acepta sesiones antiguas como autenticación.

## Verificación
Ejecuta `.\mvnw.cmd test`. Las pruebas levantan un servidor HTTP real con H2 en memoria, comprueban los cuatro roles, firma alterada, expiración, logout y ausencia de cookie de sesión. No se necesita MySQL para los tests.

## Matriz completa de endpoints
| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/admin/palabras` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| POST | `/api/admin/palabras` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| PUT | `/api/admin/palabras/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| PATCH | `/api/admin/palabras/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| DELETE | `/api/admin/palabras/{id}` | hasAnyRole('ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/sugerencias` | hasAnyRole('ADMINISTRADOR','SUPER_USUARIO') |
| PATCH | `/api/admin/sugerencias/{id}/estado` | hasAnyRole('ADMINISTRADOR','SUPER_USUARIO') |
| POST | `/api/admin/audio/upload` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| POST | `/api/auth/register` | permitAll() |
| POST | `/api/auth/login` | permitAll() |
| GET | `/api/auth/me` | isAuthenticated() |
| POST | `/api/auth/logout` | isAuthenticated() |
| GET | `/api/diccionario/buscar` | permitAll() |
| GET | `/api/diccionario/palabras` | permitAll() |
| GET | `/api/diccionario/palabras/{id}` | permitAll() |
| GET | `/api/diccionario/categorias` | permitAll() |
| GET | `/api/diccionario/lenguas` | permitAll() |
| GET | `/api/admin/lenguas` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/buscar` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/region/{regionId}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/region` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/familia` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/{id}/resumen` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/lenguas/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| POST | `/api/admin/lenguas` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| PUT | `/api/admin/lenguas/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| PATCH | `/api/admin/lenguas/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| DELETE | `/api/admin/lenguas/{id}` | hasAnyRole('ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/sugerencias/mias` | isAuthenticated() |
| POST | `/api/sugerencias` | isAuthenticated() |
| PUT | `/api/sugerencias/{id}` | isAuthenticated() |
| DELETE | `/api/sugerencias/{id}` | isAuthenticated() |
| GET | `/api/admin/traducciones` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/traducciones/buscar` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/traducciones/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/traducciones/palabra/{palabraId}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/admin/traducciones/lengua/{lenguaId}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| POST | `/api/admin/traducciones` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| PUT | `/api/admin/traducciones/{id}` | hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO') |
| DELETE | `/api/admin/traducciones/{id}` | hasAnyRole('ADMINISTRADOR','SUPER_USUARIO') |
| GET | `/api/usuario/perfil` | isAuthenticated() |
| PUT | `/api/usuario/perfil` | isAuthenticated() |
| PUT | `/api/usuario/password` | isAuthenticated() |
| GET | `/api/usuario/favoritos` | isAuthenticated() |
| POST | `/api/usuario/favoritos/{palabraId}` | isAuthenticated() |
| DELETE | `/api/usuario/favoritos/{palabraId}` | isAuthenticated() |
| GET | `/api/usuario/historial` | isAuthenticated() |
| DELETE | `/api/usuario/historial` | isAuthenticated() |
| POST | `/api/usuarios` | hasRole('SUPER_USUARIO') |
| GET | `/api/usuarios` | hasRole('SUPER_USUARIO') |
| GET | `/api/usuarios/{id}` | hasRole('SUPER_USUARIO') |
| PUT | `/api/usuarios/{id}` | hasRole('SUPER_USUARIO') |
| PATCH | `/api/usuarios/{id}/rol` | hasRole('SUPER_USUARIO') |
| DELETE | `/api/usuarios/{id}` | hasRole('SUPER_USUARIO') |
