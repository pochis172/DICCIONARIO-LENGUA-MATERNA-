# Diccionario Virtual de Lenguas Maternas

Aplicación **web** académica para consultar palabras en español y obtener su traducción a una lengua materna, con significado contextual, ejemplos y audio de pronunciación.

## Tecnologías

- Java 17
- Spring Boot 4.1.0
- Spring Web MVC / API REST
- Spring Data JPA
- MySQL
- HTML5, CSS3 y JavaScript
- Maven Wrapper

## Funciones incluidas

### Público
- Buscar palabra en español.
- Filtrar por lengua.
- Ver traducción, significado y ejemplos.
- Reproducir audio cuando exista.
- Explorar categorías.
- Registrarse e iniciar sesión.

### Usuario registrado
- Guardar y eliminar favoritos.
- Consultar y limpiar historial de búsquedas.
- Enviar sugerencias de nuevas palabras.
- Editar/eliminar sugerencias mientras estén pendientes.
- Consultar estado de la sugerencia.
- Editar nombre y cambiar contraseña.

### Administrador
- Crear, consultar, editar y eliminar palabras.
- Gestionar traducción, significado, ejemplo y audio.
- Subir archivos de audio (MP3, WAV, OGG, M4A).
- Ver sugerencias.
- Convertir una sugerencia en palabra oficial.
- Aprobar/rechazar/devolver a pendiente una sugerencia.

## Base de datos

La implementación usa las entidades documentadas en el proyecto:

`roles`, `usuarios`, `regiones`, `lenguas`, `palabras`, `traducciones`, `ejemplos`, `audios`, `historial`, `favoritos` y `sugerencias`.

### Notas de diseño

- La base de datos es MySQL, como indica el diagrama de componentes.
- Las traducciones se relacionan con `id_lengua`, porque el modelo entidad-relación no define una entidad `IDIOMA`.
- El significado y la categoría se guardan en `palabras`, según el diagrama de clases.
- La recuperación de contraseña por correo no está incluida: requiere un proveedor de correo y un flujo de tokens.

## Cómo abrirlo en Visual Studio Code

1. Extrae la carpeta `diccionario-lengua-materna`.
2. Abre **esa carpeta completa** en Visual Studio Code.
3. Verifica que Java 17 o superior esté instalado.
4. Inicia MySQL/XAMPP.
5. La configuración por defecto espera:

```text
Servidor: localhost
Puerto: 3306
Usuario: root
Contraseña: vacía
Base de datos: diccionario_lengua_materna
```

La base se crea automáticamente si el usuario de MySQL tiene permisos.

La aplicación necesita la variable `JWT_SECRET` (mínimo 32 caracteres) para firmar los tokens. En PowerShell:

```powershell
$env:JWT_SECRET="una-clave-larga-de-al-menos-32-caracteres"
.\mvnw.cmd spring-boot:run
```

Si tu MySQL tiene contraseña, define también `DB_PASSWORD` antes de iniciar:

```powershell
$env:DB_PASSWORD="TU_CONTRASEÑA"
```

Para probar sin MySQL puedes usar la base H2 local con el perfil `local` (no requiere `JWT_SECRET`):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Luego abre:

```text
http://localhost:8080
```

## Usuarios de demostración

Se crean al iniciar la aplicación. Todos usan la contraseña `123456`.

| Correo | Rol |
|---|---|
| kevin_administrador@diccionario.local | ADMINISTRADOR |
| alexis_superusuario@diccionario.local | SUPER_USUARIO |
| vanessa_editor@diccionario.local | EDITOR |
| karen_usuario@diccionario.local | USUARIO |

Cambia estas credenciales antes de usar el proyecto fuera de un entorno académico.

## Datos iniciales

Al iniciar por primera vez se crean algunos registros de demostración inspirados en los mockups del proyecto: Agua → Yaku, Casa → Wasi, Perro → Allku, Sol → Inti y Montaña → Urku, asociados a Quechua.

## Estructura principal

```text
src/main/java/com/diccionario/lenguamaterna/
├── config/
├── controller/
├── dto/
├── entity/
├── repository/
├── service/
└── DiccionarioLenguaMaternaApplication.java

src/main/resources/static/
├── css/
├── js/
├── index.html
├── login.html
├── registro.html
├── perfil.html
└── admin.html
```

## API principal

- `GET /api/diccionario/buscar?q=Agua`
- `GET /api/diccionario/palabras`
- `GET /api/diccionario/categorias`
- `GET /api/diccionario/lenguas`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/usuario/favoritos`
- `GET /api/usuario/historial`
- `POST /api/sugerencias`
- `GET /api/admin/palabras`
- `POST /api/admin/palabras`
- `GET /api/admin/sugerencias`

## Documentación incluida

La carpeta `docs/diagramas/` contiene una copia de los archivos PlantUML suministrados y diagramas actualizados para la implementación web.
