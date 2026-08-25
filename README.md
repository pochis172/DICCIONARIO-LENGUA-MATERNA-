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

### Decisiones de implementación

1. El demo entregado traía PostgreSQL, pero el diagrama de componentes del proyecto especifica **MySQL**, por eso esta versión usa MySQL.
2. En una página del diccionario de datos aparece `ID_IDIOMA` dentro de TRADUCCIÓN, pero el diagrama entidad-relación no define una entidad IDIOMA. Para evitar una relación huérfana, la versión implementada usa `id_lengua`, que sí existe en el modelo y en las relaciones documentadas.
3. El significado y la categoría se incluyen en PALABRAS porque aparecen en el diagrama de clases, el alcance funcional y los mockups web.
4. La recuperación de contraseña por correo queda fuera de esta primera versión porque requiere un proveedor de correo y flujo de tokens. El resto del inicio de sesión y registro sí funciona.

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

Si tu MySQL tiene contraseña, en PowerShell puedes usar:

```powershell
$env:DB_PASSWORD="TU_CONTRASEÑA"
.\mvnw.cmd spring-boot:run
```

Con root sin contraseña:

```powershell
.\mvnw.cmd spring-boot:run
```

Luego abre:

```text
http://localhost:8080
```

## Usuario administrador de demostración

```text
Correo: admin@diccionario.local
Contraseña: Admin123*
```

Cámbialo antes de usar el proyecto fuera de un entorno académico.

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
