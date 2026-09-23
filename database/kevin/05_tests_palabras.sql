USE diccionario_lengua_materna;

-- =====================================================
-- 1. VISTA
-- =====================================================

SELECT *
FROM vw_palabras_con_contenido
ORDER BY palabra_espanol;


-- =====================================================
-- 2. PROCEDIMIENTO
-- =====================================================

CALL sp_palabras_por_categoria('Naturaleza');


-- =====================================================
-- 3. FUNCION
-- =====================================================

SELECT fn_total_palabras_lengua(1)
    AS total_palabras_lengua_1;


-- =====================================================
-- 4. PRUEBA DE LEFT JOIN + TRIGGER TRIM
-- =====================================================
-- Esta prueba crea temporalmente una palabra sin
-- traducciones, ejemplos ni audios.
-- Debe aparecer en la vista con los conteos en 0.
-- Ademas, el trigger debe quitar espacios de
-- palabra_espanol.

START TRANSACTION;

SET @lengua_prueba = (
    SELECT MIN(id_lengua)
    FROM lenguas
);

INSERT INTO palabras (
    palabra_espanol,
    categoria,
    significado,
    id_lengua
)
VALUES (
    '   PruebaTriggerKevin   ',
    'Prueba',
    'Registro temporal para probar LEFT JOIN y TRIM',
    @lengua_prueba
);

SET @palabra_prueba = LAST_INSERT_ID();

SELECT
    id_palabra,
    palabra_espanol,
    categoria,
    total_traducciones,
    total_ejemplos,
    total_audios
FROM vw_palabras_con_contenido
WHERE id_palabra = @palabra_prueba;

ROLLBACK;

SELECT COUNT(*) AS total_despues_rollback
FROM palabras
WHERE id_palabra = @palabra_prueba;


-- =====================================================
-- 5. PRUEBA TRIGGER: TEXTO VACIO
-- =====================================================
-- EJECUTAR ESTA SENTENCIA POR SEPARADO.
-- Debe FALLAR con el mensaje:
-- palabra_espanol no puede estar vacia

/*
INSERT INTO palabras (
    palabra_espanol,
    categoria,
    significado,
    id_lengua
)
VALUES (
    '     ',
    'Prueba',
    'Debe ser rechazada por el trigger',
    1
);
*/


-- =====================================================
-- 6. PRUEBA FK INVALIDA
-- =====================================================
-- EJECUTAR ESTA SENTENCIA POR SEPARADO.
-- Debe FALLAR porque id_lengua no existe.

/*
INSERT INTO palabras (
    palabra_espanol,
    categoria,
    significado,
    id_lengua
)
VALUES (
    'PruebaFKKevin',
    'Prueba',
    'Debe fallar por integridad referencial',
    999999999
);
*/


-- =====================================================
-- 7. PRUEBA NOT NULL
-- =====================================================
-- EJECUTAR ESTA SENTENCIA POR SEPARADO.
-- Debe FALLAR porque categoria es NOT NULL.

/*
INSERT INTO palabras (
    palabra_espanol,
    categoria,
    significado,
    id_lengua
)
VALUES (
    'PruebaNotNullKevin',
    NULL,
    'Debe fallar por NOT NULL',
    1
);
*/
