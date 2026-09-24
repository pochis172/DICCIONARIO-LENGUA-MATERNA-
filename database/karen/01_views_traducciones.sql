USE diccionario_lengua_materna;

-- =====================================================
-- VISTA 1: Traducciones con palabra y lengua
-- Une las tablas traducciones, palabras y lenguas.
-- =====================================================

DROP VIEW IF EXISTS vw_traducciones_detalle;

CREATE VIEW vw_traducciones_detalle AS
SELECT
    t.id_traduccion,
    p.id_palabra,
    p.palabra_espanol,
    l.id_lengua,
    l.nombre AS lengua,
    t.traduccion
FROM traducciones AS t
INNER JOIN palabras AS p
    ON t.id_palabra = p.id_palabra
INNER JOIN lenguas AS l
    ON t.id_lengua = l.id_lengua;


-- =====================================================
-- VISTA 2: Total de traducciones por lengua
-- =====================================================

DROP VIEW IF EXISTS vw_total_traducciones_lengua;

CREATE VIEW vw_total_traducciones_lengua AS
SELECT
    l.id_lengua,
    l.nombre AS lengua,
    COUNT(t.id_traduccion) AS total_traducciones
FROM lenguas AS l
LEFT JOIN traducciones AS t
    ON l.id_lengua = t.id_lengua
GROUP BY
    l.id_lengua,
    l.nombre;
    