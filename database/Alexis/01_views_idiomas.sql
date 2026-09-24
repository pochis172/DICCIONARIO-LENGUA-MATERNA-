USE diccionario_lengua_materna;

-- =====================================================
-- VISTA 1: Lenguas con su región
-- INNER JOIN:
-- muestra cada lengua junto con la región a la que pertenece.
-- =====================================================

DROP VIEW IF EXISTS vw_lenguas_regiones;

CREATE VIEW vw_lenguas_regiones AS
SELECT
    l.id_lengua,
    l.nombre AS lengua,
    l.familia_linguistica,
    r.id_region,
    r.nombre_region AS region
FROM lenguas AS l
INNER JOIN regiones AS r
    ON l.id_region = r.id_region;


-- =====================================================
-- VISTA 2: Regiones con sus lenguas
-- LEFT JOIN:
-- muestra todas las regiones aunque alguna todavía
-- no tenga lenguas registradas.
-- =====================================================

DROP VIEW IF EXISTS vw_regiones_lenguas;

CREATE VIEW vw_regiones_lenguas AS
SELECT
    r.id_region,
    r.nombre_region AS region,
    l.id_lengua,
    l.nombre AS lengua,
    l.familia_linguistica
FROM regiones AS r
LEFT JOIN lenguas AS l
    ON r.id_region = l.id_region;