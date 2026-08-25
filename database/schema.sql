CREATE DATABASE IF NOT EXISTS diccionario_lengua_materna
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE diccionario_lengua_materna;

CREATE TABLE IF NOT EXISTS roles (
  id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
  id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(120) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  id_rol BIGINT NOT NULL,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE IF NOT EXISTS regiones (
  id_region BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre_region VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS lenguas (
  id_lengua BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  id_region BIGINT NOT NULL,
  familia_linguistica VARCHAR(100),
  CONSTRAINT fk_lengua_region FOREIGN KEY (id_region) REFERENCES regiones(id_region)
);

CREATE TABLE IF NOT EXISTS palabras (
  id_palabra BIGINT AUTO_INCREMENT PRIMARY KEY,
  palabra_espanol VARCHAR(120) NOT NULL,
  categoria VARCHAR(80) NOT NULL,
  significado VARCHAR(500) NOT NULL,
  id_lengua BIGINT NOT NULL,
  INDEX idx_palabra_espanol (palabra_espanol),
  INDEX idx_palabra_categoria (categoria),
  CONSTRAINT fk_palabra_lengua FOREIGN KEY (id_lengua) REFERENCES lenguas(id_lengua)
);

CREATE TABLE IF NOT EXISTS traducciones (
  id_traduccion BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_palabra BIGINT NOT NULL,
  id_lengua BIGINT NOT NULL,
  traduccion VARCHAR(150) NOT NULL,
  CONSTRAINT fk_traduccion_palabra FOREIGN KEY (id_palabra) REFERENCES palabras(id_palabra),
  CONSTRAINT fk_traduccion_lengua FOREIGN KEY (id_lengua) REFERENCES lenguas(id_lengua)
);

CREATE TABLE IF NOT EXISTS ejemplos (
  id_ejemplo BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_palabra BIGINT NOT NULL,
  ejemplo VARCHAR(255) NOT NULL,
  CONSTRAINT fk_ejemplo_palabra FOREIGN KEY (id_palabra) REFERENCES palabras(id_palabra)
);

CREATE TABLE IF NOT EXISTS audios (
  id_audio BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_palabra BIGINT NOT NULL,
  url_audio VARCHAR(255) NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  CONSTRAINT fk_audio_palabra FOREIGN KEY (id_palabra) REFERENCES palabras(id_palabra)
);

CREATE TABLE IF NOT EXISTS historial (
  id_historial BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario BIGINT NOT NULL,
  id_palabra BIGINT NOT NULL,
  fecha DATETIME NOT NULL,
  CONSTRAINT fk_historial_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
  CONSTRAINT fk_historial_palabra FOREIGN KEY (id_palabra) REFERENCES palabras(id_palabra)
);

CREATE TABLE IF NOT EXISTS favoritos (
  id_favorito BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario BIGINT NOT NULL,
  id_palabra BIGINT NOT NULL,
  CONSTRAINT uk_favorito_usuario_palabra UNIQUE (id_usuario, id_palabra),
  CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
  CONSTRAINT fk_favorito_palabra FOREIGN KEY (id_palabra) REFERENCES palabras(id_palabra)
);

CREATE TABLE IF NOT EXISTS sugerencias (
  id_sugerencia BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario BIGINT NOT NULL,
  palabra_sugerida VARCHAR(100) NOT NULL,
  posible_traduccion VARCHAR(150),
  id_lengua BIGINT,
  descripcion VARCHAR(255),
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  fecha_creacion DATETIME NOT NULL,
  CONSTRAINT fk_sugerencia_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
  CONSTRAINT fk_sugerencia_lengua FOREIGN KEY (id_lengua) REFERENCES lenguas(id_lengua)
);
