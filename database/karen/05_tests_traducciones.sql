USE diccionario_lengua_materna;

-- =====================================================
-- 1. PRUEBA DE LA VISTA DETALLADA
-- Muestra traducción + palabra + lengua
-- =====================================================

SELECT *
FROM vw_traducciones_detalle
ORDER BY id_traduccion;


-- =====================================================
-- 2. PRUEBA DE LA VISTA DE TOTALES
-- Cuenta traducciones por cada lengua
-- =====================================================

SELECT *
FROM vw_total_traducciones_lengua
ORDER BY id_lengua;


-- =====================================================
-- 3. PRUEBA DEL PROCEDIMIENTO ALMACENADO
-- Lengua 1 = Quechua
-- =====================================================

CALL sp_traducciones_por_lengua(1);


-- =====================================================
-- 4. PRUEBA DE LA FUNCIÓN
-- Cuenta traducciones de la lengua 1
-- =====================================================

SELECT fn_total_traducciones_lengua(1)
AS total_traducciones_quechua;


-- =====================================================
-- 5. COMPROBAR LLAVES FORÁNEAS
-- traducciones -> palabras
-- traducciones -> lenguas
-- =====================================================

SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'diccionario_lengua_materna'
  AND TABLE_NAME = 'traducciones'
  AND REFERENCED_TABLE_NAME IS NOT NULL;


-- =====================================================
-- 6. COMPROBAR ÍNDICES
-- =====================================================

SHOW INDEX FROM traducciones;


-- =====================================================
-- 7. COMPROBAR TRIGGERS
-- =====================================================

SHOW TRIGGERS
FROM diccionario_lengua_materna;


-- =====================================================
-- 8. PRUEBA DEL TRIGGER BEFORE INSERT + TRIM
-- Se crea un registro temporal con espacios.
-- El trigger debe quitarlos.
-- ROLLBACK evita dejar datos de prueba.
-- =====================================================

START TRANSACTION;

INSERT INTO traducciones (
    id_palabra,
    id_lengua,
    traduccion
)
VALUES (
    1,
    2,
    '     Prueba Trigger Karen     '
);

SET @id_prueba_karen = LAST_INSERT_ID();

SELECT
    id_traduccion,
    id_palabra,
    id_lengua,
    traduccion
FROM traducciones
WHERE id_traduccion = @id_prueba_karen;

ROLLBACK;


-- =====================================================
-- 9. PRUEBA DEL TRIGGER BEFORE UPDATE
-- Debe quitar espacios al actualizar.
-- Tampoco deja datos permanentes.
-- =====================================================

START TRANSACTION;

INSERT INTO traducciones (
    id_palabra,
    id_lengua,
    traduccion
)
VALUES (
    1,
    2,
    'Prueba Update Karen'
);

SET @id_update_karen = LAST_INSERT_ID();

UPDATE traducciones
SET traduccion = '     Traduccion Actualizada Karen     '
WHERE id_traduccion = @id_update_karen;

SELECT
    id_traduccion,
    traduccion
FROM traducciones
WHERE id_traduccion = @id_update_karen;

ROLLBACK;


-- =====================================================
-- 10. PRUEBA DE TRADUCCIÓN VACÍA
-- EJECUTAR POR SEPARADO.
-- DEBE FALLAR gracias al trigger.
-- =====================================================

/*

INSERT INTO traducciones (
    id_palabra,
    id_lengua,
    traduccion
)
VALUES (
    1,
    2,
    '      '
);

*/


-- =====================================================
-- 11. PRUEBA DE FOREIGN KEY INVÁLIDA
-- EJECUTAR POR SEPARADO.
-- DEBE FALLAR porque palabra 999999 no existe.
-- =====================================================

/*

INSERT INTO traducciones (
    id_palabra,
    id_lengua,
    traduccion
)
VALUES (
    999999,
    1,
    'Prueba FK Karen'
);

*/
