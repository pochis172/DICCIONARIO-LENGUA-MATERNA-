USE diccionario_lengua_materna;

-- =====================================================
-- 1. PRUEBA DE VISTAS
-- =====================================================

SELECT * FROM vw_lenguas_regiones;

SELECT * FROM vw_regiones_lenguas;


-- =====================================================
-- 2. PRUEBA DEL PROCEDIMIENTO ALMACENADO
-- =====================================================

CALL sp_lenguas_por_region(2);

CALL sp_lenguas_por_region(1);


-- =====================================================
-- 3. PRUEBA DE LA FUNCIÓN
-- =====================================================

SELECT fn_total_lenguas_region(2)
AS total_lenguas_amazonia;

SELECT fn_total_lenguas_region(1)
AS total_lenguas_andina;

SELECT
    r.id_region,
    r.nombre_region,
    fn_total_lenguas_region(r.id_region)
        AS total_lenguas
FROM regiones AS r
ORDER BY r.id_region;


-- =====================================================
-- 4. PRUEBA DE FOREIGN KEY
-- Lengua -> Región
-- =====================================================

SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'diccionario_lengua_materna'
  AND TABLE_NAME = 'lenguas'
  AND REFERENCED_TABLE_NAME IS NOT NULL;


-- =====================================================
-- 5. PRUEBA DE ÍNDICES
-- =====================================================

SHOW INDEX FROM regiones;

SHOW INDEX FROM lenguas;


-- =====================================================
-- 6. COMPROBAR LOS TRIGGERS
-- =====================================================

SHOW TRIGGERS
FROM diccionario_lengua_materna;


-- =====================================================
-- 7. PRUEBA DE TRIGGER + TRANSACCIÓN
-- La prueba no deja datos permanentes.
-- =====================================================

START TRANSACTION;

INSERT INTO lenguas (
    nombre,
    id_region,
    familia_linguistica
)
VALUES (
    '   Lengua Prueba SQL   ',
    2,
    '   Familia Prueba SQL   '
);

SELECT
    id_lengua,
    nombre,
    familia_linguistica,
    id_region
FROM lenguas
WHERE nombre = 'Lengua Prueba SQL';

ROLLBACK;


-- Comprobar que el rollback eliminó la prueba
SELECT *
FROM lenguas
WHERE nombre = 'Lengua Prueba SQL';